package com.astromyllc.shootingstar.setup.model;

import com.sun.mail.iap.ByteArray;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "preorder_institution")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PreOrderInstitution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 
    private Long idInstitution;
    @Column(nullable = false)
    private String name;
    private String slogan;
    @Column(nullable = false)
    private String country;
    private String region;
    @Column(nullable = false)
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
    private Integer streams;
    @Column(nullable = false)
    private String subscription;
    @Column(nullable = false)
    private String population;
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "TEXT")
    private String crest;

}
