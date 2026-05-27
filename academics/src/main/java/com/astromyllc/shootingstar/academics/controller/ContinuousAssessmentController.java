package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.ContinuousAssessmentServiceInterface;
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
public class ContinuousAssessmentController {
    private final ContinuousAssessmentServiceInterface continuousAssessmentServiceInterface;
    @PostMapping("/api/academics/submitContinuousAssessment")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<ContinuousAssessmentResponse> SubmitContinuousAssessment(@RequestBody ContinuousAssessmentRequest continuousAssessmentRequest) {
        log.info("Application  Received");
       return continuousAssessmentServiceInterface.submitContinuousAssessment(continuousAssessmentRequest);
    }

    @PostMapping("/api/academics/submitContinuousAssessmentLList")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<ContinuousAssessmentResponse> SubmitContinuousAssessmentList(@RequestBody List<ContinuousAssessmentRequest> continuousAssessmentRequest) {
        log.info("Application  Received");
       return continuousAssessmentServiceInterface.submitContinuousAssessments(continuousAssessmentRequest);
    }
}
