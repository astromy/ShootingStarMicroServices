package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Entity
@Table(name = "institution")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 
    private Long idInstitution;
    @NonNull
    private String name;
    private String slogan;
    @NonNull
    private String country;
    private String region;
    private String city;
    @NonNull
    private String email;
    private String website;
    @NonNull
    private String contact1;
    private String contact2;
    private String status;
    @Column(unique=true)
    @NonNull
    private String bececode;
    private LocalDate creationDate;
    private String postalAddress;
    @NonNull
    private Integer streams;
    private String subscription;
    private String crest;

    @OneToOne(targetEntity = GradingSetting.class,cascade = CascadeType.ALL)
    private GradingSetting gradingSetting;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Subject.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionSubject",referencedColumnName = "idInstitution")
    private List<Subject> subjectList;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Classes.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionClass",referencedColumnName = "idInstitution")
    private List<Classes> classList;
    @OneToOne(targetEntity = Admissions.class,cascade = CascadeType.ALL)
    private Admissions admissions;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Department.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionDepartment",referencedColumnName = "idInstitution")
    private List<Department> departmentList;
}
