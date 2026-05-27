package com.astromyllc.shootingstar.onlineapplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApplicationsWithImageResponse {
    private String idapplication;
    private String fatherFirstNames;
    private String fatherLastName;
    private String fatherEmail;
    private String fatherContact1;
    private String fatherContact2;
    private String fatherOccupation;
    private String fatherPlaceOfWork;

    private String motherFirstNames;
    private String motherLastName;
    private String motherEmail;
    private String motherContact1;
    private String motherContact2;
    private String motherOccupation;
    private String motherPlaceOfWork;

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
    private String applicationType;
    private LocalDate applicationDate;
    private LocalDateTime appointmentDate;

    private String nameOfPreviousSchool;
    private String classOfDeparture;
    private String reasonForDeparture;
    private String addressOfPreviousSchool;
    private String contactOfPreviousSchool;
}
