package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.model.Assessment;
import com.astromyllc.shootingstar.academics.serviceInterface.AssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.AssessmentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentService implements AssessmentServiceInterface {
private final AssessmentUtil assessmentUtil;

 /*   @Override
    public AssessmentResponse generateTerminalReport(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.buildAssessmentRequest(terminalReportRequest);
       Assessment assessment= assessmentUtil.insertAssessment(terminalReportRequest);
        return  assessment.stream().map(a-> assessmentUtil.mapAssessment_ToAssessmentResponse(a)));
    }*/

    @Override
    public List<AssessmentResponse> generateTerminalReports(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.buildAssessmentRequest(terminalReportRequest);
       List<Assessment> assessmentList= assessmentUtil.insertAssessment(terminalReportRequest);
      return  assessmentList.stream().map(a-> assessmentUtil.mapAssessment_ToAssessmentResponse(a)).collect(Collectors.toList());
    }


}
