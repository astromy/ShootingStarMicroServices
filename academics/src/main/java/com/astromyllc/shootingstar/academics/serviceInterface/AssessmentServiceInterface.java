package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.dto.response.TerminalReportResponse;

import java.util.List;
import java.util.Optional;

public interface AssessmentServiceInterface {

/*    public AssessmentResponse generateTerminalReport(AcademicReportRequest terminalReportRequest);*/
    public Optional<TerminalReportResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest);
}
