package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    private GradingSettingRequest gradingSetting;
    private List<SubjectRequest> subjectList;
    private List<ClassesRequest> classList;
    private AdmissionsRequest admissions;
    private List<DepartmentRequest> departmentList;
}
