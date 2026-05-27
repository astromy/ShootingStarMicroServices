package com.astromyllc.shootingstar.onlineapplication.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexOptions;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(value="applications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Applications {

	@Id
	@Field("id")
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
	private String applicantDenomination;

	@Indexed(unique = true)
	@NonNull
	private String applicationCode;
	private String applicationType;
	private String applicationStatus;
	@NonNull
	private String applicationInstitution;
	@NonNull
	private LocalDate applicationDate;
	private LocalDateTime appointmentDate;

	private String nameOfPreviousSchool;
	private String classOfDeparture;
	private String reasonForDeparture;
	private String addressOfPreviousSchool;
	private String contactOfPreviousSchool;
	
}
