package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentsImportRequest {
    private String id;
    private String studentId;
    private String firstName;
    private String otherName;
    private String lastName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfAdmission;
    private String placeOfBirth;
    private String gender;
    private String countryOfBirth;
    private String residentialLocality;
    private String picture;
    private String birthCert;
    private String denomination;
    private String institutionCode;
    private String studentClass;
    private String status;
    private List<ParentsRequest> parentsRequests;
}
