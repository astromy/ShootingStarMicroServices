package com.astromyllc.shootingstar.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TerminalReportResponse {
    private ReportInstitutionResponse institutionDetail;
    private List<StudentReportResponse> studentReportResponseList;
}
