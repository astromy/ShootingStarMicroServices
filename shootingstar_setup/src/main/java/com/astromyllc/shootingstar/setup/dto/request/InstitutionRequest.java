package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InstitutionRequest {
    private Long id;
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
    private String creationDate;
    @NonNull
    private String bececode;
    private String postalAddress;
    @NonNull
    private Integer streams;
    private String subscription;
    private String crest;

    private GradingSettingDetails gradingSetting;
    private List<SubjectDetails> subjectList;
    private List<ClassDetail> classList;
    private AdmissionsRequest admissions;
    private List<DepartmentDetails> departmentList;
}
