package com.astromyllc.shootingstar.hr.dto.response;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class ProfessionalRecordsResponse {
    private String id;
    private String nameOfInstitution;
    private LocalDate dateOfEmployment;
    private LocalDate dateOfDeparture;
    private String designationAtInstitution;
    private String employmentTypeAtInstitution;

}
