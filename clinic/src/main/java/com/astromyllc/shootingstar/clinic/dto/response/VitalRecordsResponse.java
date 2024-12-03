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
public class VitalRecordsResponse {
    private Long id;
    private LocalDateTime dateTime;
    private String recordType;
    private String value;
    private String patientId;
    private String patientType;
}
