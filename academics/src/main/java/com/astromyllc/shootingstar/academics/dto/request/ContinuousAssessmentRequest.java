package com.astromyllc.shootingstar.academics.dto.request;

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
public class ContinuousAssessmentRequest {
    private Long id;
    private Double score;
    private Double totalScore;
    private Long subject;
    private String term;
    private String studentClass;
    private String academicYear;
    private String studentId;
    private String dateTime;
    private String institutionCode;
}
