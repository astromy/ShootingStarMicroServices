package com.astromyllc.shootingstar.adminpta.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(value="students")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
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

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity = StudentSubjects.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "studentSubjects",referencedColumnName = "id")
    private List<StudentSubjects> studentSubjects;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity = Parents.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "parentsList",referencedColumnName = "id")
    private List<Parents> parentsList;
}
