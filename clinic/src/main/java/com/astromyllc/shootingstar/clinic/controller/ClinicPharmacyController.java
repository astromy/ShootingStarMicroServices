package com.astromyllc.shootingstar.clinic.controller;

import com.astromyllc.shootingstar.clinic.dto.request.InventoryRequest;
import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductRequest;
import com.astromyllc.shootingstar.clinic.serviceInterface.InventoryServiceInterface;
import com.astromyllc.shootingstar.clinic.serviceInterface.MedicalProductServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The clinic's pharmacy / stock module — medical products (the catalogue)
 * and inventory movements (restock / dispense against that catalogue).
 * Previously totally unimplemented (empty service classes); this is the
 * missing piece the "finish the Clinic component" work never covered.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClinicPharmacyController {

    private final MedicalProductServiceInterface medicalProductServiceInterface;
    private final InventoryServiceInterface inventoryServiceInterface;

    // ── MEDICAL PRODUCTS (catalogue) ────────────────────────────────────
    @PostMapping("/api/clinic/pharmacy/createProduct")
    public ResponseEntity<?> createProduct(@RequestBody MedicalProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalProductServiceInterface.createProduct(request));
    }

    @PostMapping("/api/clinic/pharmacy/getProductsByInstitution")
    public ResponseEntity<?> getProductsByInstitution(@RequestBody MedicalProductFetchRequest request) {
        return ResponseEntity.ok(medicalProductServiceInterface.fetchProductsByInstitution(request));
    }

    @PostMapping("/api/clinic/pharmacy/getLowStockProducts")
    public ResponseEntity<?> getLowStockProducts(@RequestBody MedicalProductFetchRequest request) {
        return ResponseEntity.ok(medicalProductServiceInterface.fetchLowStockProducts(request));
    }

    @PostMapping("/api/clinic/pharmacy/deactivateProduct/{productId}")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long productId) {
        try {
            return ResponseEntity.ok(medicalProductServiceInterface.deactivateProduct(productId));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // ── INVENTORY MOVEMENTS (restock / dispense) ────────────────────────
    @PostMapping("/api/clinic/pharmacy/recordMovement")
    public ResponseEntity<?> recordMovement(@RequestBody InventoryRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(inventoryServiceInterface.recordMovement(request));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/api/clinic/pharmacy/getMovementsByInstitution")
    public ResponseEntity<?> getMovementsByInstitution(@RequestBody MedicalProductFetchRequest request) {
        return ResponseEntity.ok(inventoryServiceInterface.fetchMovementsByInstitution(request.getInstitutionCode()));
    }

    @PostMapping("/api/clinic/pharmacy/getMovementsByProduct/{productId}")
    public ResponseEntity<?> getMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryServiceInterface.fetchMovementsByProduct(productId));
    }
}
