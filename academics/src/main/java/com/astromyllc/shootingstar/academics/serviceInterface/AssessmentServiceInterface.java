package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;

import java.util.List;

public interface AssessmentServiceInterface {

/*    public AssessmentResponse generateTerminalReport(AcademicReportRequest terminalReportRequest);*/
    public List<AssessmentResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest);
}
