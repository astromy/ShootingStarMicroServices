package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.serviceInterface.AssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.AssessmentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentService implements AssessmentServiceInterface {
private final AssessmentUtil assessmentUtil;

    @Override
    public void generateTerminalReport(AcademicReportRequest terminalReportRequest) {
        assessmentUtil.buildAssessmentRequest(terminalReportRequest);
        assessmentUtil.insertAssessment(terminalReportRequest);
    }
}
