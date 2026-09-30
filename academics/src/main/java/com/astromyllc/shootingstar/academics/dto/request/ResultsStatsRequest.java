package com.astromyllc.shootingstar.academics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ResultsStatsRequest {
    private String institutionCode;
    private String term;          // optional; omit for the most recent term
    private String academicYear;  // optional; omit for the most recent year
}
