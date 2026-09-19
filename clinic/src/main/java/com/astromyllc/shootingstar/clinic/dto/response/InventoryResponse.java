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
public class InventoryResponse {
    private Long id;
    private String institutionCode;
    private Long medicalProductId;
    private String productName;
    private String movementType;
    private Integer quantity;
    private String patientId;
    private String patientType;
    private Long visitId;
    private String recordedBy;
    private LocalDateTime dateTime;
}