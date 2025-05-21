package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentScores {
    private String studentId;
    private Double totalScoreObtained;
    private Double totalScorePossible;
    @Builder.Default
    private boolean hasNullData = false;

    // Public method to check for null data
    public boolean hasNullData() {
        return this.hasNullData ||
                this.studentId == null ||
                this.totalScoreObtained == null ||
                this.totalScorePossible == null;
    }

    // Static merge method
    public static StudentScores merge(StudentScores s1, StudentScores s2) {
        return StudentScores.builder()
                .studentId(s1.studentId != null ? s1.studentId : s2.studentId)
                .totalScoreObtained(sumPreservingNulls(s1.totalScoreObtained, s2.totalScoreObtained))
                .totalScorePossible(sumPreservingNulls(s1.totalScorePossible, s2.totalScorePossible))
                .hasNullData(s1.hasNullData() || s2.hasNullData())
                .build();
    }

    // Alias for merge (mergePreservingNulls)
    public static StudentScores mergePreservingNulls(StudentScores s1, StudentScores s2) {
        return merge(s1, s2);
    }

    private static Double sumPreservingNulls(Double a, Double b) {
        if (a == null) return b;
        if (b == null) return a;
        return a + b;
    }
}