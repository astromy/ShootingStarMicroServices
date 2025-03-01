package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentScores {
    private String studentId;
    private Double totalScoreObtained;
    private Double totalScorePossible;

   /* public StudentScores() {
        this.totalScoreObtained = 0.0;
        this.totalScorePossible = 0.0;
    }*/

   /* public StudentScores(String studentId, Double score, Double totalScore) {
        this.studentId = studentId;
        this.totalScoreObtained = score != null ? score : 0.0;
        this.totalScorePossible = totalScore != null ? totalScore : 0.0;
    }*/

    public static StudentScores merge(StudentScores s1, StudentScores s2) {
        return new StudentScores(
                s1.studentId != null ? s1.studentId : s2.studentId,
                s1.totalScoreObtained + s2.totalScoreObtained,
                s1.totalScorePossible + s2.totalScorePossible
        );
    }

    public String getStudentId() { return studentId; }
    public Double getTotalScoreObtained() { return totalScoreObtained; }
    public Double getTotalScorePossible() { return totalScorePossible; }
}