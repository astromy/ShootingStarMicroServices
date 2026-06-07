package com.astromyllc.shootingstar.onlineapplication.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(value = "applications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Applications {

    @Id
    @Field("id")
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
    private String applicantDenomination;

    @Indexed(unique = true)
    @NonNull
    private String applicationCode;
    private String applicationType;
    private String applicationStatus;
    @NonNull
    private String applicationInstitution;
    private String applicationInstitutionName;
    @NonNull
    private LocalDate applicationDate;
    private LocalDateTime appointmentDate;

    private String nameOfPreviousSchool;
    private String classOfDeparture;
    private String reasonForDeparture;
    private String addressOfPreviousSchool;
    private String contactOfPreviousSchool;

    @DBRef
    private List<Parents> parentsList;

}
