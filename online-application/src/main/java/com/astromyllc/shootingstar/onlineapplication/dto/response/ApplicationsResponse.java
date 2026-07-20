package com.astromyllc.shootingstar.onlineapplication.dto.response;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ParentsRequest;
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
public class ApplicationsResponse {
    private String idapplication;

    private String applicantFirstName;
    private String applicantOtherName;
    private String applicantLastName;
    private LocalDate applicantDateOfBirth;
    private String applicantPlaceOfBirth;
    private String applicantGender;
    private String applicantCountryOfBirth;
    private String applicantNationality;
    private String applicantPicture;
    private String applicantBirthCert;
    private String applicantDenomination;


    private String applicationCode;
    private String applicationStatus;
    private String applicationInstitution;
    private String applicationInstitutionName;
    private String applicationType;
    private LocalDate applicationDate;
    private LocalDate admissionDate;
    private LocalDateTime appointmentDate;

    private String nameOfPreviousSchool;
    private String classOfDeparture;
    private String reasonForDeparture;
    private String addressOfPreviousSchool;
    private String contactOfPreviousSchool;
    private List<ParentsRequest> studentParents;
}
