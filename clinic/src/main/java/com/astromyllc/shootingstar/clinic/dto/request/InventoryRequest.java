package com.astromyllc.shootingstar.clinic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Covers both a restock and a dispense — movementType distinguishes them.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InventoryRequest {
    private String institutionCode;
    private Long medicalProductId;
    private Integer quantity;
    private String movementType;   // RESTOCK | DISPENSE

    // Only used when movementType == DISPENSE
    private String patientId;
    private String patientType;
    private Long visitId;

    private String recordedBy;
}