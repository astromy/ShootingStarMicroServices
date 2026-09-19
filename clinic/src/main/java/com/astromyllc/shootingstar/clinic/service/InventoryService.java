package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.InventoryRequest;
import com.astromyllc.shootingstar.clinic.dto.response.InventoryResponse;
import com.astromyllc.shootingstar.clinic.model.Inventory;
import com.astromyllc.shootingstar.clinic.model.MedicalProduct;
import com.astromyllc.shootingstar.clinic.repository.InventoryRepository;
import com.astromyllc.shootingstar.clinic.repository.MedicalProductRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.InventoryServiceInterface;
import com.astromyllc.shootingstar.clinic.util.InventoryUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stock movements against MedicalProduct — RESTOCK adds to both totalStock
 * (cumulative, ever received) and availableStock (currently on hand);
 * DISPENSE only reduces availableStock, since totalStock is a historical
 * figure. Same distinction stores-inventory's StoreItem makes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService implements InventoryServiceInterface {

    private final InventoryRepository inventoryRepository;
    private final MedicalProductRepository medicalProductRepository;
    private final InventoryUtil inventoryUtil;

    @Override
    public InventoryResponse recordMovement(InventoryRequest request) {
        MedicalProduct product = medicalProductRepository.findById(request.getMedicalProductId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No medical product found with id " + request.getMedicalProductId()));

        int quantity = request.getQuantity() == null ? 0 : request.getQuantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        boolean isDispense = "DISPENSE".equalsIgnoreCase(request.getMovementType());
        if (isDispense) {
            int currentAvailable = product.getAvailableStock() == null ? 0 : product.getAvailableStock();
            if (quantity > currentAvailable) {
                throw new IllegalStateException(
                        "Cannot dispense " + quantity + " of " + product.getName()
                                + " — only " + currentAvailable + " available");
            }
            product.setAvailableStock(currentAvailable - quantity);
        } else {
            product.setTotalStock((product.getTotalStock() == null ? 0 : product.getTotalStock()) + quantity);
            product.setAvailableStock((product.getAvailableStock() == null ? 0 : product.getAvailableStock()) + quantity);
        }
        medicalProductRepository.save(product);

        Inventory movement = Inventory.builder()
                .institutionCode(request.getInstitutionCode())
                .medicalProductId(product.getId())
                .productName(product.getName())
                .movementType(isDispense ? "DISPENSE" : "RESTOCK")
                .quantity(quantity)
                .patientId(isDispense ? request.getPatientId() : null)
                .patientType(isDispense ? request.getPatientType() : null)
                .visitId(isDispense ? request.getVisitId() : null)
                .recordedBy(request.getRecordedBy())
                .dateTime(LocalDateTime.now())
                .build();
        movement = inventoryRepository.save(movement);

        log.info("{} of {} x{} at institution {} (by {})",
                movement.getMovementType(), product.getName(), quantity,
                request.getInstitutionCode(), request.getRecordedBy());

        return inventoryUtil.mapInventory_ToInventoryResponse(movement);
    }

    @Override
    public List<InventoryResponse> fetchMovementsByInstitution(String institutionCode) {
        return inventoryRepository.findByInstitutionCodeOrderByDateTimeDesc(institutionCode)
                .stream().map(inventoryUtil::mapInventory_ToInventoryResponse).toList();
    }

    @Override
    public List<InventoryResponse> fetchMovementsByProduct(Long medicalProductId) {
        return inventoryRepository.findByMedicalProductIdOrderByDateTimeDesc(medicalProductId)
                .stream().map(inventoryUtil::mapInventory_ToInventoryResponse).toList();
    }
}
