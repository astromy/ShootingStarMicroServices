package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.serviceInterface.ContinuousAssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.ContinuousAssessmentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ContinuousAssessmentController {
    private final ContinuousAssessmentServiceInterface continuousAssessmentServiceInterface;
    @PostMapping("/api/academics/submitContinuousAssessment")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitContinuousAssessment(@RequestBody ContinuousAssessmentRequest continuousAssessmentRequest) {
        log.info("Application  Received");
        continuousAssessmentServiceInterface.submitContinuousAssessment(continuousAssessmentRequest);
    }

    @PostMapping("/api/academics/submitContinuousAssessmentLList")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitContinuousAssessmentList(@RequestBody List<ContinuousAssessmentRequest> continuousAssessmentRequest) {
        log.info("Application  Received");
        continuousAssessmentServiceInterface.submitContinuousAssessments(continuousAssessmentRequest);
    }
}
