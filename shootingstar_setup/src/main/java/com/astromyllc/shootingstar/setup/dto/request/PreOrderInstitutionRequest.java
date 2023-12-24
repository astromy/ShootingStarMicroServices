package com.astromyllc.shootingstar.setup.dto.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PreOrderInstitutionRequest {
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
    @Column(unique=true)
    private String bececode;
    private LocalDate creationDate;
    private String postalAddress;
    private Integer streams;
    private String subscription;
    private String population;
    private String crest;

}
