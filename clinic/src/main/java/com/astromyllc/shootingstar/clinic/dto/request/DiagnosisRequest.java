package com.astromyllc.shootingstar.clinic.dto.request;

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
public class DiagnosisRequest {
    private Long id;
    private String dateTime;
    private String diagnosis;
    private String patientId;
    private String patientType;
    private String institutionCode;
}
