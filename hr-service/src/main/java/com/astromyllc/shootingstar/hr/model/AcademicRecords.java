package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Entity
@Document(value = "academic_records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "id")
public class AcademicRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String nameOfInstitution;
    private LocalDate dateOfAdmission;
    private LocalDate dateOfGraduation;
    private String programOffered;
    private String certificateType;
    private String supportingDocs;
    private String staffAcademicRecords;
    private String institutionCode;

}
