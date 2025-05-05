package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String picture;
    private String birthCert;
    private String denomination;
    private String institutionCode;
    private String status;
    private String studentClass;
    private List<ParentsResponse> studentParents;
    private List<StudentSubjectsResponse> studentSubjectsResponse;
}
