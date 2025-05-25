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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    public Optional<TerminalReportResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest) {
        if (assessmentUtil.singleInstitutionGlobalRequest == null) {
            initializeData(terminalReportRequest);
        }

        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = calculateContinuousScores(terminalReportRequest);
        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = calculateExamScores(terminalReportRequest);

        Optional<StudentScores> highestTotalScore = findHighestTotalScore(continuousResult);
        List<Assessment> continuous = processContinuousAssessments(continuousResult, terminalReportRequest, highestTotalScore);
        List<Assessment> exams = processExamAssessments(examsResult, terminalReportRequest);

        List<Assessment> mergedAssessments = mergeAssessments(continuous, exams);
        List<Assessment> assessmentsWithPosition = processAssessmentsWithPosition(mergedAssessments);

        saveAssessments(assessmentsWithPosition);

        TerminalReportResponse result = buildTerminalReportResponse(assessmentsWithPosition, terminalReportRequest);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<TerminalReportResponse> generateBroadsheet(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.getApplicableGradingSetting(terminalReportRequest);

        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = calculateContinuousScores(terminalReportRequest);
        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = calculateExamScores(terminalReportRequest);

        Optional<StudentScores> highestTotalScore = findHighestTotalScore(continuousResult);
        List<Assessment> continuous = processContinuousAssessments(continuousResult, terminalReportRequest, highestTotalScore);
        List<Assessment> exams = processExamAssessments(examsResult, terminalReportRequest);

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

    private Optional<StudentScores> findHighestTotalScore(Map<String, Map<Long, Map<String, StudentScores>>> continuousResult) {
        return continuousResult.values().parallelStream()
                .flatMap(subjectMap -> subjectMap.values().parallelStream())
                .flatMap(studentMap -> studentMap.values().parallelStream())
                .filter(s -> s != null && s.getTotalScorePossible() != null)
                .max(Comparator.comparingDouble(StudentScores::getTotalScorePossible));
    }

    private List<Assessment> processContinuousAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> continuousResult,
            AcademicReportRequest request,
            Optional<StudentScores> highestTotalScore) {
        return continuousResult.entrySet().parallelStream()
                .flatMap(sr -> assessmentUtil.passContinuousAssessment(sr, request, highestTotalScore.orElse(null)).parallelStream())
                .collect(Collectors.toList());
    }

    private List<Assessment> processExamAssessments(
            Map<String, Map<Long, Map<String, StudentScores>>> examsResult,
            AcademicReportRequest request) {
        return examsResult.entrySet().parallelStream()
                .flatMap(sr -> assessmentUtil.passExamsAssessment(sr, request).parallelStream())
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
                        return continuousAssessment;
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
        Map<String, Assessment> existingAssessments = assessmentUtil.assessmentsGlobalList.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        a -> String.join("_", a.getStudentId(), a.getSubject(), a.getTerm(),
                                a.getAcademicYear(), a.getInstitutionCode()),
                        Function.identity()));

        List<Assessment> toSave = assessments.parallelStream()
                .map(newAssessment -> {
                    String key = String.join("_",
                            newAssessment.getStudentId(),
                            newAssessment.getSubject(),
                            newAssessment.getTerm(),
                            newAssessment.getAcademicYear(),
                            newAssessment.getInstitutionCode());

                    Assessment existing = existingAssessments.get(key);
                    if (existing != null) {
                        existing.setClassScore(newAssessment.getClassScore());
                        existing.setExamsScore(newAssessment.getExamsScore());
                        existing.setTotalScore(newAssessment.getTotalScore());
                        existing.setGrade(newAssessment.getGrade());
                        return existing;
                    } else {
                        assessmentUtil.assessmentsGlobalList.add(newAssessment);
                        return newAssessment;
                    }
                })
                .collect(Collectors.toList());

        assessmentRepository.saveAll(toSave);
    }

    private TerminalReportResponse buildTerminalReportResponse(List<Assessment> assessments, AcademicReportRequest request) {
        return assessmentUtil.buildTerminalReportResponse(
                assessments.parallelStream()
                        .map(awp -> assessmentUtil.mapAssessment_ToAssessmentResponse(awp, request.getClassGroup()))
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