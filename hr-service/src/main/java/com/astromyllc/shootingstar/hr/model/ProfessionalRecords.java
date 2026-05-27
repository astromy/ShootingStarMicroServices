package com.astromyllc.shootingstar.hr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Entity
@Document(value = "professional_records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "id")
public class ProfessionalRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String nameOfInstitution;
    private LocalDate dateOfEmployment;
    private LocalDate dateOfDeparture;
    private String designationAtInstitution;
    private String employmentTypeAtInstitution;
    private String staffProfessionalRecords;
    private String supportingDocs;
    private String institutionCode;
}
