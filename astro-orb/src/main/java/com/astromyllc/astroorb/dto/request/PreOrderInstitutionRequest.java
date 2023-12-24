package com.astromyllc.astroorb.dto.request;

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
    private String bececode;
    private String postalAddress;
    private LocalDate creationDate;
    private Integer streams;
    private String subscription;
    private String population;
    private String crest;

}
