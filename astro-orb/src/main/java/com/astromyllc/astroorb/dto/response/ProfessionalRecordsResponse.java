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
public class ProfessionalRecordsResponse {
    private String id;
    private String nameOfInstitution;
    private LocalDate dateOfEmployment;
    private LocalDate dateOfDeparture;
    private String designationAtInstitution;
    private String employmentTypeAtInstitution;
    private String supportingDocs;

}
