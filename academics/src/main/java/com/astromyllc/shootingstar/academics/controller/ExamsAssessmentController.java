package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.ExamsAssessmentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
