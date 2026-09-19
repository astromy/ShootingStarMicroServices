package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.dto.response.InventoryResponse;
import com.astromyllc.shootingstar.clinic.model.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryUtil {

    public InventoryResponse mapInventory_ToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .institutionCode(inventory.getInstitutionCode())
                .medicalProductId(inventory.getMedicalProductId())
                .productName(inventory.getProductName())
                .movementType(inventory.getMovementType())
                .quantity(inventory.getQuantity())
                .patientId(inventory.getPatientId())
                .patientType(inventory.getPatientType())
                .visitId(inventory.getVisitId())
                .recordedBy(inventory.getRecordedBy())
                .dateTime(inventory.getDateTime())
                .build();
    }
}
