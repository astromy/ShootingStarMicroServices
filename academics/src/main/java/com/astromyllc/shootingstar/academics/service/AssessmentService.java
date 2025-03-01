package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.alien.StudentScores;
import com.astromyllc.shootingstar.academics.dto.alien.Students;
import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
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

 /*   @Override
    public AssessmentResponse generateTerminalReport(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.buildAssessmentRequest(terminalReportRequest);
       Assessment assessment= assessmentUtil.insertAssessment(terminalReportRequest);
        return  assessment.stream().map(a-> assessmentUtil.mapAssessment_ToAssessmentResponse(a)));
    }*/

    @Override
    public Optional<TerminalReportResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest) {

        Map<String, Map<Long, Map<String, StudentScores>>> continuousResult = ContinuousAssessmentUtil.CalculateScores(
                ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream()
                        .filter(ca -> ca.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass())
                                && ca.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear())
                                && ca.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm())
                                && ca.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()))
                        .collect(Collectors.toList()));


        Map<String, Map<Long, Map<String, StudentScores>>> examsResult = ExamsAssessmentUtil.CalculateExamsScores(
                ExamsAssessmentUtil.examsAssessmentGlobalList.stream()
                        .filter(ca -> ca.getStudentClass().equalsIgnoreCase(terminalReportRequest.getTargetClass())
                                && ca.getAcademicYear().equalsIgnoreCase(terminalReportRequest.getAcademicYear())
                                && ca.getTerm().equalsIgnoreCase(terminalReportRequest.getTerm())
                                && ca.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode()))
                        .collect(Collectors.toList()));


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
                        continuousAssessment.setTotalScore(Math.round((continuousAssessment.getClassScore() != null ? continuousAssessment.getClassScore() : 0)
                                + examAssessment.getExamsScore()* 100.0) / 100.0);
                       // continuousAssessment.setGrade(examAssessment.getGrade());
                    }

                    log.info("Merged Student: =>", continuousAssessment);
                    return continuousAssessment;
                })
                .collect(Collectors.toList());





       List<Assessment> assessmentsListWithoutPosition= mergedAssessments.stream().map(assessmentUtil::buildAssessment).collect(Collectors.toList());
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
        TerminalReportResponse result=assessmentUtil.buildTerminalReportResponse(assessmentsListWithPosition.stream().map(awp->assessmentUtil.mapAssessment_ToAssessmentResponse(awp,terminalReportRequest.getClassGroup())).collect(Collectors.toList()), assessmentUtil.studentsGlobalRequest);
        return Optional.ofNullable(result);
    }


}
