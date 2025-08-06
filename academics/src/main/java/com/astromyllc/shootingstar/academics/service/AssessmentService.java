package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.alien.StudentScores;
import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.dto.response.ExistingUploadedScoreResponse;
import com.astromyllc.shootingstar.academics.dto.response.TerminalReportResponse;
import com.astromyllc.shootingstar.academics.model.Assessment;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.AssessmentRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.AssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.AssessmentUtil;
import com.astromyllc.shootingstar.academics.util.ContinuousAssessmentUtil;
import com.astromyllc.shootingstar.academics.util.ExamsAssessmentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentService implements AssessmentServiceInterface {
    private final AssessmentUtil assessmentUtil;
    private final ContinuousAssessmentUtil continuousAssessmentUtil;
    private final ExamsAssessmentUtil examsAssessmentUtil;
    private final AssessmentRepository assessmentRepository;

    // Cache for frequently accessed data
    private final Map<String, List<Assessment>> assessmentCache = new ConcurrentHashMap<>();
    private final Map<String, TerminalReportResponse> reportCache = new ConcurrentHashMap<>();

    @Override
    public Optional<TerminalReportResponse> fetchStudentTerminalReport(AcademicReportRequest terminalReportRequest) {
        String cacheKey = buildCacheKey(terminalReportRequest);
        if (reportCache.containsKey(cacheKey)) {
            return Optional.of(reportCache.get(cacheKey));
        }

        initializeData(terminalReportRequest);

        List<Assessment> assessmentList = getFilteredAssessments(terminalReportRequest);
        TerminalReportResponse result = assessmentUtil.getTerminalReportResponse(
                parallelMapAssessments(assessmentList, terminalReportRequest.getClassGroup()),
                terminalReportRequest);

        reportCache.put(cacheKey, result);
        return Optional.ofNullable(result);
    }

    @Override
    public void PostStudentReports(AcademicReportRequest terminalReportRequest) {
        initializeData(terminalReportRequest);

        List<Assessment> assessmentList = getFilteredAssessments(terminalReportRequest);
        TerminalReportResponse result = assessmentUtil.getTerminalReportResponseWithParent(
                parallelMapAssessments(assessmentList, terminalReportRequest.getClassGroup()),
                terminalReportRequest);

        assessmentUtil.sendSMS(result.getStudentReportResponseList(), result.getInstitutionDetail().getName());
    }

    @Override
    public Optional<TerminalReportResponse> fetchStudentTranscript(SingleStringRequest terminalReportRequest) {
        List<Assessment> assessmentList = AssessmentUtil.assessmentsGlobalList.parallelStream()
                .filter(ar -> ar.getStudentId().equalsIgnoreCase(terminalReportRequest.getVal()))
                .collect(Collectors.toList());

        TerminalReportResponse result = assessmentUtil.getTerminalReportResponse(
                assessmentList.parallelStream()
                        .map(assessmentUtil::mapAssessment_ToAssessmentResponse)
                        .collect(Collectors.toList()),
                terminalReportRequest);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<ExistingUploadedScoreResponse>> getExistingClassSubjectScores(AcademicReportRequest terminalReportRequest) {
        List<ExistingUploadedScoreResponse> existingUploadedScoreResponse = AssessmentUtil.assessmentsGlobalList.parallelStream()
                .filter(eus -> eus.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear()) &&
                        eus.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass()) &&
                        eus.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()) &&
                        eus.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm()))
                .map(assessmentUtil::mapAssessment_To_ExistingUploadedScoreResponse)
                .collect(Collectors.toList());

        return Optional.ofNullable(existingUploadedScoreResponse);
    }

    @Override
    public Optional<TerminalReportResponse> getStudentAcademicYearReport(SingleStringRequest studentID) {
        if (assessmentUtil.singleInstitutionGlobalRequest == null) {
            Optional<String> institutionCode = assessmentUtil.assessmentsGlobalList.parallelStream()
                    .filter(a -> a.getStudentId().equalsIgnoreCase(studentID.getVal()))
                    .findAny()
                    .map(Assessment::getInstitutionCode);
            initializeData(institutionCode.get());
        }

        List<Assessment> assessments = assessmentUtil.assessmentsGlobalList.parallelStream()
                .filter(a -> a.getStudentId().equalsIgnoreCase(studentID.getVal()))
                /*.filter(a -> a.getPosition() != null)*/
                .collect(Collectors.groupingBy(
                        Assessment::getAcademicYear,
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList());

        return assessments.isEmpty()
                ? Optional.empty()
                : Optional.of(buildTerminalReportResponse(assessments))
                .map(response -> {
                    if (response.getInstitutionDetail() != null) {
                        response.getInstitutionDetail().setCrest(null); // Nullify crest
                        response.getInstitutionDetail().setHeadSignature(null);
                    }
                    return response; // Return the modified response
                });
    }

    @Override
    public Optional<TerminalReportResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest) {
        if (assessmentUtil.singleInstitutionGlobalRequest == null) {
            initializeData(terminalReportRequest);
        }

        // Get latest continuous and exam scores
        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = calculateContinuousScores(terminalReportRequest);
        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = calculateExamScores(terminalReportRequest);

        // Find highest scores per subject group
        Map<String, Map<Long, Double>> highestScoresPerSubject = findHighestScoresPerSubjectGroup(continuousResult);

        // Process assessments with latest records only
        List<Assessment> continuous = processContinuousAssessments(continuousResult, terminalReportRequest,
                highestScoresPerSubject, assessmentUtil)
                .stream()
                .filter(a -> a.getClassScore() != null) // Filter out null scores
                .collect(Collectors.toList());

        List<Assessment> exams = processExamAssessments(examsResult, terminalReportRequest)
                .stream()
                .filter(a -> a.getExamsScore() != null) // Filter out null scores
                .collect(Collectors.toList());

        // Merge assessments ensuring uniqueness
        List<Assessment> mergedAssessments = mergeLatestAssessments(continuous, exams);

        if (mergedAssessments.isEmpty()) {
            return Optional.empty();
        }

        // Process positions and save
        List<Assessment> assessmentsWithPosition = processAssessmentsWithPosition(mergedAssessments);
        saveAssessments(assessmentsWithPosition);

        TerminalReportResponse result = buildTerminalReportResponse(assessmentsWithPosition, terminalReportRequest);
        return Optional.ofNullable(result);
    }

    // New method to merge assessments ensuring only latest records
    private List<Assessment> mergeLatestAssessments(List<Assessment> continuous, List<Assessment> exams) {
        // Create a map keyed by studentId + subject + term + academicYear
        Map<String, Assessment> mergedMap = new HashMap<>();

        // Process continuous assessments
        continuous.forEach(assessment -> {
            String key = generateAssessmentKey(assessment);
            Assessment existing = mergedMap.get(key);

            if (existing != null) {
                // Only update if the incoming score is not null
                if (assessment.getClassScore() != null) {
                    existing.setClassScore(assessment.getClassScore());
                }
            } else {
                mergedMap.put(key, assessment);
            }
        });

        // Process exam assessments
        exams.forEach(exam -> {
            String key = generateAssessmentKey(exam);
            Assessment existing = mergedMap.get(key);

            if (existing != null) {
                // Only update if the incoming score is not null
                if (exam.getExamsScore() != null) {
                    existing.setExamsScore(exam.getExamsScore());
                }
            } else {
                mergedMap.put(key, exam);
            }
        });

        // Calculate total scores and filter
        return mergedMap.values().stream()
                .map(assessment -> {
                    // Set null scores to 0 for calculation
                    Double classScore = assessment.getClassScore() != null ? assessment.getClassScore() : 0.0;
                    Double examScore = assessment.getExamsScore() != null ? assessment.getExamsScore() : 0.0;

                    // Only calculate total if at least one score exists
                    if (classScore != 0.0 || examScore != 0.0) {
                        assessment.setTotalScore(Math.round((classScore + examScore) * 100.0) / 100.0);
                        return assessment;
                    }
                    return null; // Will be filtered out
                })
                .filter(Objects::nonNull) // Remove records with no valid scores
                .filter(a -> a.getTotalScore() > 0) // Only keep records with positive total score
                .collect(Collectors.toList());
    }


    @Override
    public Optional<TerminalReportResponse> generateBroadsheet(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.getApplicableGradingSetting(terminalReportRequest);

        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = calculateContinuousScores(terminalReportRequest);
        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = calculateExamScores(terminalReportRequest);

        // Changed to get highest score per subject
        Map<String, Map<Long, Double>> highestScoresPerSubject = findHighestScoresPerSubjectGroup(continuousResult);

        List<Assessment> continuous = processContinuousAssessments(continuousResult, terminalReportRequest, highestScoresPerSubject, assessmentUtil);
        List<Assessment> exams = processExamAssessments(examsResult, terminalReportRequest);

        List<Assessment> mergedAssessments = mergeAssessmentsForBroadSheet(continuous, exams);

        TerminalReportResponse result = buildBroadSheetResponse(mergedAssessments, terminalReportRequest);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<TerminalReportResponse> generateUnconvertedBroadsheet(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.getApplicableGradingSetting(terminalReportRequest);

        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = calculateContinuousScores(terminalReportRequest);
        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = calculateExamScores(terminalReportRequest);

        // Changed to get highest score per subject
        Map<String, Map<Long, Double>> highestScoresPerSubject = findHighestScoresPerSubjectGroup(continuousResult);

        List<Assessment> continuous = processRawContinuousAssessments(continuousResult, terminalReportRequest, highestScoresPerSubject, assessmentUtil);
        List<Assessment> exams = processRawExamAssessments(examsResult, terminalReportRequest);

        List<Assessment> mergedAssessments = mergeAssessmentsForBroadSheet(continuous, exams);

        TerminalReportResponse result = buildBroadSheetResponse(mergedAssessments, terminalReportRequest);
        return Optional.ofNullable(result);
    }

    // ========== PRIVATE OPTIMIZED METHODS ==========

    private void initializeData(AcademicReportRequest request) {
        assessmentUtil.fetchSetupdata(request.getInstitutionCode());
        assessmentUtil.fetchStudents(request.getInstitutionCode());
        assessmentUtil.fetchClassGroups(request.getInstitutionCode());
        assessmentUtil.getApplicableGradingSetting(request);
    }

    private void initializeData(String request) {
        assessmentUtil.fetchSetupdata(request);
        assessmentUtil.fetchStudents(request);
        assessmentUtil.fetchClassGroups(request);
    }

    private List<Assessment> getFilteredAssessments(AcademicReportRequest request) {
        String cacheKey = buildCacheKey(request);
        return assessmentCache.computeIfAbsent(cacheKey, key ->
                AssessmentUtil.assessmentsGlobalList.parallelStream()
                        .filter(ar -> ar.getStudentClass().equalsIgnoreCase(request.getTargetClass()) &&
                                ar.getAcademicYear().equalsIgnoreCase(request.getAcademicYear()) &&
                                ar.getTerm().equalsIgnoreCase(request.getTerm()) &&
                                ar.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode()))
                        .collect(Collectors.toList())
        );
    }

    private List<AssessmentResponse> parallelMapAssessments(List<Assessment> assessments, String classGroup) {
        return assessments.parallelStream()
                .map(awp -> assessmentUtil.mapAssessment_ToAssessmentResponse(awp, classGroup))
                .collect(Collectors.toList());
    }

    private Map<String, Map<Long, Map<String, StudentScores>>> calculateContinuousScores(AcademicReportRequest request) {
        return ContinuousAssessmentUtil.CalculateScores(
                ContinuousAssessmentUtil.continuousAssessmentGlobalList.parallelStream()
                        .filter(ca -> ca.getStudentClass().equalsIgnoreCase(request.getTargetClass()) &&
                                ca.getAcademicYear().equalsIgnoreCase(request.getAcademicYear()) &&
                                ca.getTerm().equalsIgnoreCase(request.getTerm()) &&
                                ca.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode()))
                        .collect(Collectors.groupingByConcurrent(
                                ca -> new AbstractMap.SimpleEntry<>(ca.getStudentId(), ca.getSubject()),
                                Collectors.maxBy(Comparator.comparing(ContinuousAssessment::getDateTime)))
                        )
                        .values()
                        .parallelStream()
                        .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
                        .collect(Collectors.toList())
        );
    }

    private Map<String, Map<Long, Map<String, StudentScores>>> calculateExamScores(AcademicReportRequest request) {
        return ExamsAssessmentUtil.CalculateExamsScores(
                ExamsAssessmentUtil.examsAssessmentGlobalList.parallelStream()
                        .filter(ca -> ca.getStudentClass().equalsIgnoreCase(request.getTargetClass()) &&
                                ca.getAcademicYear().equalsIgnoreCase(request.getAcademicYear()) &&
                                ca.getTerm().equalsIgnoreCase(request.getTerm()) &&
                                ca.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode()))
                        .collect(Collectors.groupingByConcurrent(
                                ca -> new AbstractMap.SimpleEntry<>(ca.getStudentId(), ca.getSubject()),
                                Collectors.maxBy(Comparator.comparing(ExamsAssessment::getDateTime)))
                        )
                        .values()
                        .parallelStream()
                        .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
                        .collect(Collectors.toList())
        );
    }

  /*  private Optional<StudentScores> findHighestTotalScore(Map<String, Map<Long, Map<String, StudentScores>>> continuousResult) {
        return continuousResult.values().parallelStream()
                .flatMap(subjectMap -> subjectMap.values().parallelStream())
                .flatMap(studentMap -> studentMap.values().parallelStream())
                .filter(s -> s != null && s.getTotalScorePossible() != null)
                .max(Comparator.comparingDouble(StudentScores::getTotalScorePossible));
    }*/


    // New method to find highest score per subject
 /*   private Map<String, Optional<StudentScores>> findHighestScoresPerSubject(
            Map<String, Map<Long, Map<String, StudentScores>>> continuousResult) {
        return continuousResult.entrySet().parallelStream()
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey, // Subject as key
                        entry -> entry.getValue().values().parallelStream()
                                .flatMap(studentMap -> studentMap.values().parallelStream())
                                .filter(s -> s != null && s.getTotalScorePossible() != null)
                                .max(Comparator.comparingDouble(StudentScores::getTotalScorePossible))
                ));
    }*/

    private Map<String, Map<Long, Double>> findHighestScoresPerSubjectGroup(
            Map<String, Map<Long, Map<String, StudentScores>>> continuousResult) {

        return continuousResult.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Subject group
                        entry -> entry.getValue().entrySet().stream() // Map<Long, Map<String, StudentScores>>
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey, // Group key (e.g., 76)
                                        groupEntry -> groupEntry.getValue().values().stream()
                                                .filter(s -> s != null && s.getTotalScorePossible() != null)
                                                .mapToDouble(StudentScores::getTotalScorePossible)
                                                .max()
                                                .orElse(0.0)
                                ))
                ));
    }

 /*   private List<Assessment> processContinuousAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> continuousResult,
            AcademicReportRequest request,
            Optional<StudentScores> highestTotalScore) {
        return continuousResult.entrySet().parallelStream()
                .flatMap(sr -> assessmentUtil.passContinuousAssessment(sr, request, highestTotalScore.orElse(null)).parallelStream())
                .collect(Collectors.toList());
    }*/

    // Modified to accept per-subject highest scores
    public List<Assessment> processContinuousAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> continuousResult,
            AcademicReportRequest terminalReportRequest,
            Map<String, Map<Long, Double>> highestScoresPerSubject,
            AssessmentUtil assessmentUtil) {

        return continuousResult.entrySet().parallelStream()
                .flatMap(subjectEntry -> {
                    String subject = subjectEntry.getKey();
                    Map<Long, Double> highestPerGroup = highestScoresPerSubject.getOrDefault(subject, new HashMap<>());

                    return assessmentUtil.passContinuousAssessment(
                            subjectEntry,
                            terminalReportRequest,
                            highestPerGroup
                    ).stream();
                })
                .collect(Collectors.toList());
    }


    private List<Assessment> processRawContinuousAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> continuousResult,
            AcademicReportRequest terminalReportRequest,
            Map<String, Map<Long, Double>> highestScoresPerSubject,
            AssessmentUtil assessmentUtil) {

        return continuousResult.entrySet().parallelStream()
                .flatMap(subjectEntry -> {
                    String subject = subjectEntry.getKey();
                    Map<Long, Double> highestPerGroup = highestScoresPerSubject.getOrDefault(subject, new HashMap<>());

                    return assessmentUtil.passRawContinuousAssessment(
                            subjectEntry,
                            terminalReportRequest,
                            highestPerGroup
                    ).stream();
                })
                .collect(Collectors.toList());
    }

    private List<Assessment> processExamAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> examsResult,
            AcademicReportRequest request) {
        return examsResult.entrySet().parallelStream()
                .flatMap(sr -> assessmentUtil.passExamsAssessment(sr, request).parallelStream())
                .collect(Collectors.toList());
    }

    private List<Assessment> processRawExamAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> examsResult,
            AcademicReportRequest request) {
        return examsResult.entrySet().parallelStream()
                .flatMap(sr -> assessmentUtil.passRawExamsAssessment(sr, request).parallelStream())
                .collect(Collectors.toList());
    }

    private List<Assessment> mergeAssessments(List<Assessment> continuous, List<Assessment> exams) {
        Map<String, Assessment> examsMap = exams.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        e -> String.join("_", e.getSubject(), e.getTerm(), e.getStudentClass(),
                                e.getAcademicYear(), e.getStudentId(), e.getInstitutionCode()),
                        Function.identity(),
                        (existing, replacement) -> existing));

        return continuous.parallelStream()
                .map(continuousAssessment -> {
                    String key = String.join("_",
                            continuousAssessment.getSubject(),
                            continuousAssessment.getTerm(),
                            continuousAssessment.getStudentClass(),
                            continuousAssessment.getAcademicYear(),
                            continuousAssessment.getStudentId(),
                            continuousAssessment.getInstitutionCode());

                    Assessment examAssessment = examsMap.get(key);
                    if (examAssessment != null) {
                        continuousAssessment.setExamsScore(examAssessment.getExamsScore());
                    }

                    if (continuousAssessment.getClassScore() != null || continuousAssessment.getExamsScore() != null) {
                        double classScore = continuousAssessment.getClassScore() != null ? continuousAssessment.getClassScore() : 0.0;
                        double examScore = continuousAssessment.getExamsScore() != null ? continuousAssessment.getExamsScore() : 0.0;
                        continuousAssessment.setTotalScore(Math.round((classScore + examScore) * 100.0) / 100.0);
                        // Return the assessment only if at least one score is > 0
                        if (classScore > 0 || examScore > 0) {
                            return continuousAssessment;
                        }
                    }
                    return null;

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Assessment> mergeAssessmentsForBroadSheet(List<Assessment> continuous, List<Assessment> exams) {
        Map<String, Assessment> examsMap = exams.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        e -> String.join("_", e.getSubject(), e.getTerm(), e.getStudentClass(),
                                e.getAcademicYear(), e.getStudentId(), e.getInstitutionCode()),
                        Function.identity(),
                        (existing, replacement) -> existing));

        return continuous.parallelStream()
                .map(continuousAssessment -> {
                    String key = String.join("_",
                            continuousAssessment.getSubject(),
                            continuousAssessment.getTerm(),
                            continuousAssessment.getStudentClass(),
                            continuousAssessment.getAcademicYear(),
                            continuousAssessment.getStudentId(),
                            continuousAssessment.getInstitutionCode());

                    Assessment examAssessment = examsMap.get(key);
                    if (examAssessment != null) {
                        continuousAssessment.setExamsScore(examAssessment.getExamsScore());
                    }

                    if (continuousAssessment.getClassScore() != null &&
                            continuousAssessment.getExamsScore() != null) {
                        continuousAssessment.setTotalScore(
                                Math.round((continuousAssessment.getClassScore() +
                                        continuousAssessment.getExamsScore()) * 100.0) / 100.0
                        );
                    } else {
                        continuousAssessment.setTotalScore(null);
                        continuousAssessment.setGradeRemarks(
                                continuousAssessment.getGradeRemarks() == null ?
                                        "MISSING_SCORES" :
                                        continuousAssessment.getGradeRemarks() + ",MISSING_SCORES"
                        );
                    }
                    return continuousAssessment;
                })
                .collect(Collectors.toList());
    }

    private List<Assessment> processAssessmentsWithPosition(List<Assessment> assessments) {
        List<Assessment> builtAssessments = assessments.parallelStream()
                .map(assessmentUtil::buildAssessment)
                .collect(Collectors.toList());
        return assessmentUtil.insertPositions(builtAssessments);
    }

    private void saveAssessments(List<Assessment> assessments) {
        if (assessments == null || assessments.isEmpty()) {
            return;
        }

        // Group by unique key and keep only the latest assessment
        Map<String, Assessment> latestAssessments = assessments.stream()
                .collect(Collectors.toMap(
                        this::generateAssessmentKey,
                        Function.identity(),
                        (existing, replacement) ->
                                existing.getDateTime().isAfter(replacement.getDateTime()) ? existing : replacement
                ));

        // Create concurrent map of existing assessments for thread-safe lookups
        ConcurrentMap<String, Assessment> existingAssessments = AssessmentUtil.assessmentsGlobalList
                .parallelStream()
                .filter(Objects::nonNull)
                .collect(Collectors.toConcurrentMap(
                        a -> generateAssessmentKey(a),
                        Function.identity(),
                        (existing, replacement) -> existing // Keep first occurrence if duplicates
                ));

        // Prepare batch updates
        List<Assessment> toSave = new ArrayList<>(assessments.size());
        List<Assessment> toAddToGlobalList = Collections.synchronizedList(new ArrayList<>());

        assessments.parallelStream().forEach(newAssessment -> {
            String key = generateAssessmentKey(newAssessment);
            Assessment existing = existingAssessments.get(key);

            if (existing != null) {
                // Update existing assessment
                updateAssessmentFields(existing, newAssessment);
                toSave.add(existing);
            } else {
                // Add new assessment
                toSave.add(newAssessment);
                toAddToGlobalList.add(newAssessment);
            }
        });

        // Batch save all assessments
        assessmentRepository.saveAll(toSave);

        // Add new assessments to global list in bulk
        if (!toAddToGlobalList.isEmpty()) {
            assessmentUtil.assessmentsGlobalList.addAll(toAddToGlobalList);
        }
    }


    // Helper method to generate consistent keys
    private String generateAssessmentKey(Assessment a) {
        return String.join("_",
                a.getStudentId(),
                a.getSubject(),
                a.getTerm(),
                a.getAcademicYear(),
                a.getInstitutionCode());
    }

    // Helper method to update fields
    private void updateAssessmentFields(Assessment target, Assessment source) {
        target.setClassScore(source.getClassScore());
        target.setExamsScore(source.getExamsScore());
        target.setTotalScore(source.getTotalScore());
        target.setGrade(source.getGrade());
        target.setGradeRemarks(source.getGradeRemarks());
        target.setPosition(source.getPosition());
        target.setDateTime(LocalDateTime.now());
    }

    private TerminalReportResponse buildTerminalReportResponse(List<Assessment> assessments, AcademicReportRequest request) {
        return assessmentUtil.buildTerminalReportResponse(
                assessments.parallelStream()
                        .map(awp -> assessmentUtil.mapAssessment_ToAssessmentResponse(awp, request.getClassGroup()))
                        .collect(Collectors.toList()),
                assessmentUtil.studentsGlobalRequest);
    }

    private TerminalReportResponse buildTerminalReportResponse(List<Assessment> assessments) {
        return assessmentUtil.buildTerminalReportResponse(
                assessments.parallelStream()
                        .map(assessmentUtil::mapAssessment_ToAssessmentResponse)
                        .collect(Collectors.toList()),
                assessmentUtil.studentsGlobalRequest);
    }

    private TerminalReportResponse buildBroadSheetResponse(List<Assessment> assessments, AcademicReportRequest request) {
        return assessmentUtil.buildTerminalReportResponse(
                assessments.parallelStream()
                        .map(awp -> assessmentUtil.mapAssessment_ToAssessmentResponse_For_Broadsheet(awp, request.getClassGroup()))
                        .collect(Collectors.toList()),
                assessmentUtil.studentsGlobalRequest);
    }

    private String buildCacheKey(AcademicReportRequest request) {
        return String.join("|",
                request.getInstitutionCode(),
                request.getTargetClass(),
                request.getAcademicYear(),
                request.getTerm());
    }
}