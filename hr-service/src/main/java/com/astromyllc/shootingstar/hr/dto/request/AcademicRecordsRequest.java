package com.astromyllc.shootingstar.hr.dto.request;

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
public class AcademicRecordsRequest {
    private String id;
    private String nameOfInstitution;
    private String dateOfAdmission;
    private String dateOfGraduation;
    private String programOffered;
    private String certificateType;
    private String institutionCode;

}
