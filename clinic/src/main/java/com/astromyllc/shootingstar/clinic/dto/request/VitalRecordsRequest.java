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
public class VitalRecordsRequest {
    private Long id;
    private String dateTime;
    private String recordType;
    private String value;
    private String patientId;
    private String patientType;
    private String institutionCode;
}
