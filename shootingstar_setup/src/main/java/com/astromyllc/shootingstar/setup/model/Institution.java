package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String name;
    private String slogan;
    private String country;
    private String region;
    private String city;
    private String email;
    private String website;
    private String contact1;
    private String contact2;
    private String status;
    private String bececode;
    private LocalDate creationDate;
    private String postalAddress;
    private Integer streams;
    private String subscription;

    @OneToOne(targetEntity = GradingSetting.class,cascade = CascadeType.ALL)
    //@JoinColumn(name="institutionGradingSetting",referencedColumnName = "idInstitution")
    private GradingSetting gradingSetting;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Subject.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionSubject",referencedColumnName = "idInstitution")
    private List<Subject> subjectList;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Classes.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionClass",referencedColumnName = "idInstitution")
    private List<Classes> classList;
    @OneToOne(targetEntity = Admissions.class,cascade = CascadeType.ALL)
    //@JoinColumn(name = "institutionAdmission",referencedColumnName = "idInstitution")
    private Admissions admissions;
}
