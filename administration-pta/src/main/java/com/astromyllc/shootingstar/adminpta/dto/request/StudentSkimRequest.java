package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentSkimRequest {
    private String studentId;
    private String dateOfBirth;
    private String dateOfAdmission;
    private String gender;
    private String nationality;
    private String denomination;
    private String institutionCode;
    private String studentClass;
    private String status;
}
