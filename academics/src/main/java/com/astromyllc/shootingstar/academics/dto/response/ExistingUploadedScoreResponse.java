package com.astromyllc.shootingstar.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExistingUploadedScoreResponse {
    private Long id;
    private Double classScore;
    private Double totalClassScore;
    private Double examsScore;
    private Double totalExamsScore;
    private String subject;
    private String term;
    private String studentClass;
    private String academicYear;
    private String studentId;
    private String institutionCode;
}
