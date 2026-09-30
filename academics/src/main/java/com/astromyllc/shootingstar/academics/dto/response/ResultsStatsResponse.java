package com.astromyllc.shootingstar.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Scores are per-student averages across subjects (totalScore), then
// summarised across the school. Null scores mean nothing was assessed yet.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ResultsStatsResponse {
    private Double averageScore;
    private Double highestScore;
    private Double lowestScore;
    private long totalStudentsAssessed;
    private String term;
    private String academicYear;
}
