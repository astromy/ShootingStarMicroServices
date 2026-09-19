package com.astromyllc.shootingstar.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VisitResponse {
    private Long id;
    private String institutionCode;
    private String patientId;
    private String patientType;
    private String patientName;
    private String reason;
    private String notes;
    private String recordedBy;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;

    private Long diagnosisId;
    private Long prescriptionId;
    private Long vitalRecordsId;

    private Boolean parentNotified;
    private LocalDateTime parentNotifiedAt;
}