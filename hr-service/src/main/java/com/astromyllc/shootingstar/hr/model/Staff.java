package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Entity
@Document(value = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Indexed(unique = true)
    private String staffCode;
    private String firstNames;
    private String lastName;
    private LocalDate dateOfBirth;
    private String nationality;
    private String homeTown;
    private String residentialTown;
    private String contact1;
    private String backupContact;
    private String nationalIDType;
    @Indexed(unique = true)
    private String nationalID;
    @Indexed(unique = true)
    private String snnitNumber;
    private String maritalStatus;
    private String nameOfSpouse;
    private LocalDate dateOfEmployment;
    private String gender;
    private String level;
    private String paygrade;
    private String staffPicture;
    private String nextOfKing;


}
