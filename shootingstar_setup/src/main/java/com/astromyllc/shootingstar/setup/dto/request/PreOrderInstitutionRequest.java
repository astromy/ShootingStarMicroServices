package com.astromyllc.shootingstar.setup.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PreOrderInstitutionRequest {
    private String name;
    @NonNull
    private String slogan;
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
    @NonNull
    private Integer streams;
    @NonNull
    private String subscription;
    @NonNull
    private String population;
    @Lob
    private byte[] crest;

}
