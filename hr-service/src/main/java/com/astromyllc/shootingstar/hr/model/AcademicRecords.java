package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class AcademicRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String nameOfInstitution;
    private LocalDate dateOfAdmission;
    private LocalDate dateOfGraduation;
    private String programOffered;
    private String certificateType;
    private String staffAcademicRecords;
    private String institutionCode;

}
