package com.astromyllc.astroorb.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class StudentsImportRequest {
    private String id;
    private String studentId;
    private String firstName;
    private String otherName;
    private String lastName;
    private String dateOfBirth;
    private String dateOfAdmission;
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
