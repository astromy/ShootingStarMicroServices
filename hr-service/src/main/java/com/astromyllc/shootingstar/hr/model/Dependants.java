package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Entity
@Document(value = "dependants")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "id")
public class Dependants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String name;
    private LocalDate dateOfBirth;
    private String relationType;
    private String gender;
    private String birthCertificate;
    private String dependantPicture;
    private String staffDependant;
    private String institutionCode;
}
