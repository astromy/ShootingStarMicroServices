package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Entity
@Table(name = "\"institution\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "idInstitution")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idInstitution;
    @Column(nullable = false)
    private String name;
    private String slogan;
    @Column(nullable = false)
    private String country;
    private String region;
    private String city;
    @Column(nullable = false)
    private String email;
    private String website;
    @Column(nullable = false)
    private String contact1;
    private String contact2;
    private String status;
    @Column(nullable = false, unique=true)
    private String bececode;
    private LocalDate creationDate;
    private String postalAddress;
    @Column(nullable = false)
    private Integer streams;
    private String subscription;
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "TEXT")
    private String crest;
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "TEXT")
    private String headSignature;

    @ToString.Exclude
    @OneToOne(targetEntity = GradingSetting.class,cascade = CascadeType.ALL)
    private GradingSetting gradingSetting;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Subject.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionSubject",referencedColumnName = "idInstitution")
    private List<Subject> subjectList;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Classes.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionClass",referencedColumnName = "idInstitution")
    private List<Classes> classList;

    @ToString.Exclude
    @OneToOne(targetEntity = Admissions.class,cascade = CascadeType.ALL)
    private Admissions admissions;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Department.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "institutionDepartment",referencedColumnName = "idInstitution")
    private List<Department> departmentList;
}
