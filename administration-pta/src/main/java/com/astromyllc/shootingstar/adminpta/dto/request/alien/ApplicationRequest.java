package com.astromyllc.shootingstar.adminpta.dto.request.alien;

import com.astromyllc.shootingstar.adminpta.dto.request.ParentsRequest;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApplicationRequest {
    private String idapplication;
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
    private List<ParentsRequest> parentsRequests;
}
