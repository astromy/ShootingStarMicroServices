package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(value="students")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Students {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String studentId;
    @NonNull
    private String firstName;
    private String otherName;
    @NonNull
    private String lastName;
    @NonNull
    private LocalDate dateOfBirth;
    @NonNull
    private LocalDate dateOfAdmission;
    @NonNull
    private String placeOfBirth;
    @NonNull
    private String gender;
    @NonNull
    private String countryOfBirth;
    @NonNull
    private String residentialLocality;
    @NonNull
    private String picture;
    @NonNull
    private String birthCert;
    private String denomination;
    @NonNull
    private String institutionCode;
    private String studentClass;
    @NonNull
    private String status;
}
