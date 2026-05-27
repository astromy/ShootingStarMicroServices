package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AcademicRecordsResponse {
    private String id;
    private String nameOfInstitution;
    private LocalDate dateOfAdmission;
    private LocalDate dateOfGraduation;
    private String programOffered;
    private String certificateType;
    private String institutionCode;
    private String supportingDocs;
}
