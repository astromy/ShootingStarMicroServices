package com.astromyllc.astroorb.dto.request;

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
    private String dateFrom;   // "YYYY-MM-DD"
    private String dateTo;     // "YYYY-MM-DD"
}
