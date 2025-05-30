package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.dto.response.ExistingUploadedScoreResponse;
import com.astromyllc.shootingstar.academics.dto.response.TerminalReportResponse;

import java.util.List;
import java.util.Optional;

public interface AssessmentServiceInterface {

/*    public AssessmentResponse generateTerminalReport(AcademicReportRequest terminalReportRequest);*/
    public Optional<TerminalReportResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest);
    public Optional<TerminalReportResponse> generateBroadsheet(AcademicReportRequest terminalReportRequest);

    Optional<TerminalReportResponse>  fetchStudentTerminalReport(AcademicReportRequest terminalReportRequest);

    void PostStudentReports(AcademicReportRequest terminalReportRequest);

    Optional<TerminalReportResponse> fetchStudentTranscript(SingleStringRequest terminalReportRequest);

    Optional<List<ExistingUploadedScoreResponse>> getExistingClassSubjectScores(AcademicReportRequest terminalReportRequest);
}
