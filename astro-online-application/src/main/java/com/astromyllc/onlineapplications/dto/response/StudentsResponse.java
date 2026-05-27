package com.astromyllc.onlineapplications.dto.response;

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
public class StudentsResponse {
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
    private String birthCert;
    private String denomination;
    private String institutionCode;
    private String residentialLocality;
    private String status;
    private String studentClass;
    private List<ParentsResponse> studentParents;
    private List<StudentAccountResponse> studentAccountResponse;
    private List<StudentSubjectsResponse> studentSubjectsResponse;
    private String picture;
}
