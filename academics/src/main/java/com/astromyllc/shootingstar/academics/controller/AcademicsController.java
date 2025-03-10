package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.dto.response.TerminalReportResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.AssessmentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @PostMapping("/api/academics/generateTerminalReports")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<TerminalReportResponse>> generateTerminalReports(@RequestBody AcademicReportRequest terminalReportRequest) {
        log.info("Assessment  Received");
        return ResponseEntity.ok( assessmentServiceInterface.generateTerminalReports(terminalReportRequest));

    }
    @PostMapping("/api/academics/fetchStudentTerminalReport")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<TerminalReportResponse>> fetchStudentTerminalReport(@RequestBody AcademicReportRequest terminalReportRequest) {
        log.info("Assessment  Received");
        return ResponseEntity.ok( assessmentServiceInterface.fetchStudentTerminalReport(terminalReportRequest));

    }

    @PostMapping("/api/academics/generateStudentTranscript")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<TerminalReportResponse>> generateStudentTranscript(@RequestBody AcademicReportRequest terminalReportRequest) {
        log.info("Assessment  Received");
        return ResponseEntity.ok( assessmentServiceInterface.generateTerminalReports(terminalReportRequest));

    }
    @PostMapping("/api/academics/fetchStudentTranscript")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<TerminalReportResponse>> fetchStudentTranscript(@RequestBody SingleStringRequest terminalReportRequest) {
        log.info("Transcript Request  Received");
        return ResponseEntity.ok( assessmentServiceInterface.fetchStudentTranscript(terminalReportRequest));

    }
}
