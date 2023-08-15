package com.astromyllc.shootingstar.setup.dto.response;

import com.astromyllc.shootingstar.setup.model.Department;
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
public class InstitutionResponse {
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
    private LocalDate creationDate;
    private String bececode;
    private String postalAddress;
    private Integer streams;
    private String subscription;

    private GradingSettingResponse gradingSetting;
    private List<SubjectResponse> subjectList;
    private List<ClassesResponse> classList;
    private AdmissionsResponse admissions;
    private List<DepartmentResponse> departmentList;
}
