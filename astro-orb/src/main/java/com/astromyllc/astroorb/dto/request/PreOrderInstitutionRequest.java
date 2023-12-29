package com.astromyllc.astroorb.dto.request;

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
    private String crest;

}
