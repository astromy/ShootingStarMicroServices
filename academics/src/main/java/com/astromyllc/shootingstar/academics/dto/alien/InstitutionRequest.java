package com.astromyllc.shootingstar.academics.dto.alien;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InstitutionRequest {
    private Long id;
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
    private String creationDate;
    private String bececode;
    private String postalAddress;
    private Integer streams;
    private String subscription;
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "LONGTEXT")
    private String crest;
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "LONGTEXT")
    private String headSignature;

    private List<GradingSettingRequest> gradingSetting;
    private List<SubjectRequest> subjectList;
    private List<ClassesRequest> classList;
    //private AdmissionsRequest admissions;
}
