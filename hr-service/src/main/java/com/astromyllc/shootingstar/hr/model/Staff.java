package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Entity
@Document(collection  = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
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
    private String staffEmail;
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
    private String designation;
    private String staffPicture;
    private String nextOfKing;
    private String institutionCode;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Portfolio.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffPortfolio",referencedColumnName = "id")
    private List<Portfolio> portfolio;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Dependants.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffDependant",referencedColumnName = "id")
    private List<Dependants> dependants;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity =AcademicRecords.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffAcademicRecords",referencedColumnName = "id")
    private List<AcademicRecords> academicRecords;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity =ProfessionalRecords.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffProfessionalRecords",referencedColumnName = "id")
    private List<ProfessionalRecords> professionalRecords;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity = StaffDocuments.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffDocs",referencedColumnName = "id")
    private List<StaffDocuments> staffDocuments;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity = StaffPermissions.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffPermission",referencedColumnName = "id")
    private List<StaffPermissions> staffPermissions;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity = DesignationList.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffDesignations",referencedColumnName = "id")
    private List<DesignationList> staffDesignations;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity = StaffSubjects.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffSubjects",referencedColumnName = "id")
    private List<StaffSubjects> staffSubjects;

}
