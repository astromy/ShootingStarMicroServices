package com.astromyllc.shootingstar.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentReportResponse {
    private String id;
    private String studentId;
    private String firstName;
    private String otherName;
    private String lastName;
    private String gender;
    private String averageScore;
    private String averageGrade;
    private String averageRemark;
    private String averagePosition;
    private List<AssessmentResponse> studentAssessment;
}
