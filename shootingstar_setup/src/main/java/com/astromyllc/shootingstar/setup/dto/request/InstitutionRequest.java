package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "LONGTEXT")
    private String crest;
    @Lob // Marks this field as a Large Object (LOB)
    @Column(columnDefinition = "LONGTEXT")
    private String headSignature;

    private GradingSettingDetails gradingSetting;
    private List<SubjectDetails> subjectList;
    private List<ClassDetail> classList;
    private AdmissionsRequest admissions;
    private List<DepartmentDetails> departmentList;
}
