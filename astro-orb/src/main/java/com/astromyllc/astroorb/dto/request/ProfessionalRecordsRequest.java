package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProfessionalRecordsRequest {
    private String id;
    private String nameOfInstitution;
    private String dateOfEmployment;
    private String dateOfDeparture;
    private String designationAtInstitution;
    private String employmentTypeAtInstitution;
    private String institutionCode;
}