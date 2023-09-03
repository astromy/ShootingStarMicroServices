package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.serviceInterface.AssessmentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AcademicsController {
    private final AssessmentServiceInterface assessmentServiceInterface;
    @PostMapping("/api/academics/submitAssessment")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitAssessment(@RequestBody AcademicReportRequest terminalReportRequest) {
        log.info("Assessment  Received");
        assessmentServiceInterface.generateTerminalReport(terminalReportRequest);
    }
}
