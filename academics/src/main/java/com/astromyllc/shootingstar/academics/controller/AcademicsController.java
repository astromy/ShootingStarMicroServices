package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.AssessmentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AcademicsController {
    private final AssessmentServiceInterface assessmentServiceInterface;
  /*  @PostMapping("/api/academics/submitAssessment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AssessmentResponse>> SubmitAssessment(@RequestBody AcademicReportRequest terminalReportRequest) {
        log.info("Assessment  Received");
        return ResponseEntity.ok( assessmentServiceInterface.generateTerminalReport(terminalReportRequest));

    }*/
    @PostMapping("/api/academics/submitAssessment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AssessmentResponse>> SubmitAssessments(@RequestBody AcademicReportRequest terminalReportRequest) {
        log.info("Assessment  Received");
        return ResponseEntity.ok( assessmentServiceInterface.generateTerminalReports(terminalReportRequest));

    }
}
