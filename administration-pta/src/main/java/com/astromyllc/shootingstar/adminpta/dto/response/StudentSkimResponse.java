package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentSkimResponse {
    private String studentId;
    private String firstName;
    private String lastName;
    private String otherName;
    private String dateOfAdmission;
    private String gender;
    private String nationality;
    private String institutionCode;
    private String studentClass;
    private String picture;
    private String status;
}
