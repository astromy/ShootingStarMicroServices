package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAssessmentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExamsAssessmentController {
private final ExamsAssessmentServiceInterface examsAssessmentServiceInterface;
    @PostMapping("/api/academics/SubmitExamsAssessment")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitExamsAssessment(@RequestBody ExamsAssessmentRequest examsAssessmentRequest) {
        log.info("Application  Received");
        examsAssessmentServiceInterface.submitExamsAssessment(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/SubmitExamsAssessmentList")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitExamsAssessmentList(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
        examsAssessmentServiceInterface.submitExamsAssessments(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/fetchExamsAssessmentsForStudentPerAcademicYear")
    @ResponseStatus(HttpStatus.FOUND)
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerAcademicYear(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
      return  examsAssessmentServiceInterface.fetchExamsAssessmentsForStudentPerAcademicYear(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/fetchExamsAssessmentsForStudentPerTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerTerm(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.fetchExamsAssessmentsForStudentPerTerm(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/getExamsAssessmentByStudent")
    @ResponseStatus(HttpStatus.FOUND)
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByStudent(@RequestBody ExamsAssessmentRequest examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.getExamsAssessmentByStudent(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/fetchExamsAssessmentsForClassPerTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerTerm(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.fetchExamsAssessmentsForClassPerTerm(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/fetchExamsAssessmentsForClassPerAcademicYear")
    @ResponseStatus(HttpStatus.FOUND)
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerAcademicYear(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.fetchExamsAssessmentsForClassPerAcademicYear(examsAssessmentRequest);
    }

    @PostMapping("/api/academics/getExamsAssessmentByClass")
    @ResponseStatus(HttpStatus.FOUND)
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByClass(@RequestBody ExamsAssessmentRequest examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.getExamsAssessmentByClass(examsAssessmentRequest);
    }


    @PostMapping("/api/academics/fetchStudentProgressionReport")
    @ResponseStatus(HttpStatus.FOUND)
    public List<List<Optional<ExamsAssessmentResponse>>> fetchStudentProgressionReport(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.fetchStudentProgressionReport(examsAssessmentRequest);
    }


    @PostMapping("/api/academics/fetchPerformance")
    @ResponseStatus(HttpStatus.FOUND)
    public List<List<Optional<ExamsAssessmentResponse>>> fetchPerformance(@RequestBody List<ExamsAssessmentRequest> examsAssessmentRequest) {
        log.info("Application  Received");
        return  examsAssessmentServiceInterface.fetchPerformance(examsAssessmentRequest);
    }

}
