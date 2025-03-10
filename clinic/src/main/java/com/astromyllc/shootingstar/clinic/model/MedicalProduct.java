package com.astromyllc.shootingstar.clinic.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicalproduct")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "id")
public class MedicalProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String institutionCode;
}
