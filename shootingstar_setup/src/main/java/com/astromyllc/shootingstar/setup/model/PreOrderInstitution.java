package com.astromyllc.shootingstar.setup.model;

import com.sun.mail.iap.ByteArray;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "institution")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PreOrderInstitution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 
    private Long idInstitution;
    @NonNull
    private String name;
    private String slogan;
    @NonNull
    private String country;
    private String region;
    @NonNull
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
    private Integer streams;
    @NonNull
    private String subscription;
    @NonNull
    private String population;
    @Lob
    private byte[] crest;

}
