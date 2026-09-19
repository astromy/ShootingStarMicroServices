package com.astromyllc.shootingstar.clinic.serviceInterface;

import com.astromyllc.shootingstar.clinic.dto.request.InventoryRequest;
import com.astromyllc.shootingstar.clinic.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryServiceInterface {

    // Covers both RESTOCK and DISPENSE — movementType on the request
    // decides which. Updates the linked MedicalProduct's stock counts as
    // part of the same operation; throws IllegalStateException for a
    // DISPENSE that would take availableStock negative.
    InventoryResponse recordMovement(InventoryRequest request);

    List<InventoryResponse> fetchMovementsByInstitution(String institutionCode);

    List<InventoryResponse> fetchMovementsByProduct(Long medicalProductId);
}
