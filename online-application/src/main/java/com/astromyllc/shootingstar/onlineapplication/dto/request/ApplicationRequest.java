package com.astromyllc.shootingstar.onlineapplication.dto.request;

import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApplicationRequest {
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

    @NonNull
    private String applicantFirstName;
    private String applicantOtherName;
    @NonNull
    private String applicantLastName;
    @NonNull
    private LocalDate applicantDateOfBirth;
    @NonNull
    private String applicantPlaceOfBirth;
    @NonNull
    private String applicantGender;
    @NonNull
    private String applicantCountryOfBirth;
    @NonNull
    private String applicantNationality;
    @NonNull
    private String applicantPicture;
    @NonNull
    private String applicantBirthCert;
    private String applicantBirthCertFileType;

    @NonNull
    private String applicationInstitution;
    private String applicationType;
    private String applicationStatus;
    private String mobilemoney;

    private String applicantDenomination;
    private String nameOfPreviousSchool;
    private String classOfDeparture;
    private String reasonForDeparture;
    private String addressOfPreviousSchool;
    private String contactOfPreviousSchool;
}
