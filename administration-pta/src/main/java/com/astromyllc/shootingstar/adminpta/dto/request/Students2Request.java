package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Students2Request {
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
    private String nationality;
    private String picture;
    private String birthCert;
    private String denomination;
    private String institutionCode;
    private String residentialLocality;
    private String status;
    private String studentClass;
    private List<ParentsRequest> studentParents;
    private List<StudentSubjectsRequest> studentSubjectsList;
}
