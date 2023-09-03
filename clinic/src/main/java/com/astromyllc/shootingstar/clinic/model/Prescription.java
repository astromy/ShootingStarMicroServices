package com.astromyllc.shootingstar.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTime;
    private String prescription;
    private String patientId;
    private String patientType;
    private String institutionCode;

}
