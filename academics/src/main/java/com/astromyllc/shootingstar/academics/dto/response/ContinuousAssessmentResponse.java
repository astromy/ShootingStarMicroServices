package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContinuousAssessmentResponse {
    private Long id;
    private Double score;
    private Double totalScore;
    private String subject;
    private String term;
    private String studentClass;
    private String academicYear;
    private String studentId;
    private LocalDateTime dateTime;
    private String institutionCode;
}
