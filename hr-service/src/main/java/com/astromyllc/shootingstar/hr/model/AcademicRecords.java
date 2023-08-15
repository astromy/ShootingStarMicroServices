package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String id;
}
