package com.astromyllc.shootingstar.clinic.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PrescriptionResponse {
    private Long id;
    private LocalDateTime dateTime;
    private String prescription;
    private String patientId;
    private String patientType;
}
