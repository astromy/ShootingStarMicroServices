package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AttendanceStatsRequest {
    private String institutionCode;
    private String dateFrom;   // "YYYY-MM-DD"; defaults to the 1st of the current month
    private String dateTo;     // "YYYY-MM-DD"; defaults to today (UTC)
}
