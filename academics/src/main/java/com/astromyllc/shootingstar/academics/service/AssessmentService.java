package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.alien.StudentScores;
import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.academics.dto.response.TerminalReportResponse;
import com.astromyllc.shootingstar.academics.model.Assessment;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentService implements AssessmentServiceInterface {
    private final AssessmentUtil assessmentUtil;
    private final ContinuousAssessmentUtil continuousAssessmentUtil;
    private final ExamsAssessmentUtil examsAssessmentUtil;
    private final AssessmentRepository assessmentRepository;

   @Override
    public Optional<TerminalReportResponse> fetchStudentTerminalReport(AcademicReportRequest terminalReportRequest) {
       assessmentUtil.fetchSetupdata(terminalReportRequest.getInstitutionCode());
       assessmentUtil.fetchStudents(terminalReportRequest.getInstitutionCode());
       assessmentUtil.fetchClassGroups(terminalReportRequest.getInstitutionCode());

      List<Assessment>assessmentList=  AssessmentUtil.assessmentsGlobalList.stream()
                .filter(ar -> ar.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass())
                        && ar.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear())
                        && ar.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm())
                        && ar.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()))
              .toList();
       TerminalReportResponse result=assessmentUtil.getTerminalReportResponse(
               assessmentList.stream()
                       .map(awp->assessmentUtil.mapAssessment_ToAssessmentResponse(awp,terminalReportRequest.getClassGroup()))
                       .toList(), terminalReportRequest);
       return  Optional.ofNullable(result);
    }

    @Override
    public void PostStudentReports(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.fetchSetupdata(terminalReportRequest.getInstitutionCode());
        assessmentUtil.fetchStudents(terminalReportRequest.getInstitutionCode());
        assessmentUtil.fetchClassGroups(terminalReportRequest.getInstitutionCode());

        List<Assessment>assessmentList=  AssessmentUtil.assessmentsGlobalList.stream()
                .filter(ar -> ar.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass())
                        && ar.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear())
                        && ar.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm())
                        && ar.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()))
                .toList();
        TerminalReportResponse result=assessmentUtil.getTerminalReportResponseWithParent(
                assessmentList.stream()
                        .map(awp->assessmentUtil.mapAssessment_ToAssessmentResponse(awp,terminalReportRequest.getClassGroup()))
                        .toList(), terminalReportRequest);
        assessmentUtil.sendSMS(result.getStudentReportResponseList(),result.getInstitutionDetail().getName());
        //return  Optional.ofNullable(result);
    }

    @Override
    public Optional<TerminalReportResponse> fetchStudentTranscript(SingleStringRequest terminalReportRequest) {
        List<Assessment>assessmentList=  AssessmentUtil.assessmentsGlobalList.stream()
                .filter(ar -> ar.getStudentId().equalsIgnoreCase(terminalReportRequest.getVal()))
                .toList();
        TerminalReportResponse result=assessmentUtil.getTerminalReportResponse(
                assessmentList.stream()
                        .map(assessmentUtil::mapAssessment_ToAssessmentResponse)
                        .toList(),terminalReportRequest );
        return  Optional.ofNullable(result);
    }

    @Override
    public Optional<TerminalReportResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest) {

        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = ContinuousAssessmentUtil.CalculateScores(
                ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream()
                        .filter(ca -> ca.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass())
                                && ca.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear())
                                && ca.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm())
                                && ca.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()))
                        .toList());


        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = ExamsAssessmentUtil.CalculateExamsScores(
                ExamsAssessmentUtil.examsAssessmentGlobalList.stream()
                        .filter(ca -> ca.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass())
                                && ca.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear())
                                && ca.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm())
                                && ca.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()))
                        .toList());


        List<Assessment> resultingAssessment=new ArrayList<>();



        Optional<StudentScores> highestTotalScore = continuousResult.values().stream() // Map<String, Map<Long, Map<String, StudentScores>>>
                .flatMap(subjectMap -> subjectMap.values().stream()) // Extract Map<Long, Map<String, StudentScores>>
                .flatMap(studentMap -> studentMap.values().stream()) // Extract Map<String, StudentScores>
                .max(Comparator.comparingDouble(StudentScores::getTotalScorePossible)); // Get the max total score


        List<Assessment> continuous= continuousResult.entrySet().stream()
                .flatMap(sr -> assessmentUtil.passContinuousAssessment(sr, terminalReportRequest,highestTotalScore.get()).stream()) // Convert List to Stream
                .toList();



        List<Assessment> exams = examsResult.entrySet().stream()
                .flatMap(sr -> assessmentUtil.passExamsAssessment(sr, terminalReportRequest).stream()) // Convert List to Stream
                .toList(); // Collect all assessments


        // Convert exams list to a lookup map for quick access
        Map<String, Assessment> examsMap = exams.stream()
                .collect(Collectors.toMap(
                        e -> String.join("_", e.getSubject(), e.getTerm(), e.getStudentClass(),
                                e.getAcademicYear(), e.getStudentId(), e.getInstitutionCode()),
                        e -> e,
                        (existing, replacement) -> existing // Handle duplicates (keep the first one)
                ));


        List<Assessment> mergedAssessments = continuous.stream()
                .map(continuousAssessment -> {
                    String key = String.join("_", continuousAssessment.getSubject(), continuousAssessment.getTerm(),
                            continuousAssessment.getStudentClass(), continuousAssessment.getAcademicYear(),
                            continuousAssessment.getStudentId(), continuousAssessment.getInstitutionCode());

                    Assessment examAssessment = examsMap.get(key);
                    if (examAssessment != null) {
                        continuousAssessment.setExamsScore(examAssessment.getExamsScore());
                    }

                    // Calculate total score ONLY if either class or exam score is not null
                    if (continuousAssessment.getClassScore() != null || continuousAssessment.getExamsScore() != null) {
                        double classScore = continuousAssessment.getClassScore() != null ? continuousAssessment.getClassScore() : 0.0;
                        double examScore = continuousAssessment.getExamsScore() != null ? continuousAssessment.getExamsScore() : 0.0;
                        continuousAssessment.setTotalScore(Math.round((classScore + examScore) * 100.0) / 100.0);
                        return continuousAssessment;
                    }

                    // Return null if both are null — to be filtered out later
                    return null;
                })
                .filter(Objects::nonNull) // ✅ Remove assessments where both scores were null
                .toList();





       List<Assessment> assessmentsListWithoutPosition= mergedAssessments.stream().map(assessmentUtil::buildAssessment).toList();
        List<Assessment> assessmentsListWithPosition=assessmentUtil.insertPositions(assessmentsListWithoutPosition);


        List<Assessment> assessmentsToSave = new ArrayList<>();

        for (Assessment newAssessment : assessmentsListWithPosition) {
            // Check if assessment exists in global list
            Optional<Assessment> existingAssessmentOpt = assessmentUtil.assessmentsGlobalList.stream()
                    .filter(existing ->
                            existing.getStudentId().equals(newAssessment.getStudentId()) &&
                                    existing.getSubject().equals(newAssessment.getSubject()) &&
                                    existing.getTerm().equals(newAssessment.getTerm()) &&
                                    existing.getAcademicYear().equals(newAssessment.getAcademicYear()) &&
                                    existing.getInstitutionCode().equals(newAssessment.getInstitutionCode()))
                    .findFirst();

            if (existingAssessmentOpt.isPresent()) {
                // Update existing assessment in global list
                Assessment existingAssessment = existingAssessmentOpt.get();
                existingAssessment.setClassScore(newAssessment.getClassScore());
                existingAssessment.setExamsScore(newAssessment.getExamsScore());
                existingAssessment.setTotalScore(newAssessment.getTotalScore());
                existingAssessment.setGrade(newAssessment.getGrade());

                assessmentsToSave.add(existingAssessment); // Prepare for DB save
            } else {
                // Add new assessment to global list and prepare for DB save
                assessmentUtil.assessmentsGlobalList.add(newAssessment);
                assessmentsToSave.add(newAssessment);
            }
        }

        // Save all updates to the database
        assessmentRepository.saveAll(assessmentsToSave);
        TerminalReportResponse result=assessmentUtil.buildTerminalReportResponse(assessmentsListWithPosition.stream().map(awp->assessmentUtil.mapAssessment_ToAssessmentResponse(awp,terminalReportRequest.getClassGroup())).toList(), assessmentUtil.studentsGlobalRequest);

        return Optional.ofNullable(result);
    }


}
