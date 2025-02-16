package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssessmentRequest {
    private Long id;
    private String classScore;
    private String examsScore;
    private String totalScore;
    private String position;
    private String subject;
    private String term;
    private String studentClass;
    private String academicYear;
    private String studentId;
    private String grade;
    private String institutionCode;
    private LocalDateTime dateTime;
}
