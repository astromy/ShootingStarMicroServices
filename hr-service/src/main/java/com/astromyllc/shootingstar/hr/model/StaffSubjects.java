package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(value = "staff_subjects")
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class StaffSubjects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String subject;
    private String subjectClass;
    private String staffId;
    private String institutionCode;
}
