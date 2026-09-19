package com.astromyllc.shootingstar.clinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A stock movement against a MedicalProduct — restocking, or dispensing to
 * a patient (optionally tied to a specific Visit). Was previously a
 * near-empty skeleton (id + institutionCode). Mirrors stores-inventory's
 * StockMovement.
 */
@Entity
@Table(name = "inventory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institutionCode;
    private Long medicalProductId;
    private String productName;       // denormalized for display, same reasoning as Visit.patientName etc.

    private String movementType;      // RESTOCK | DISPENSE
    private Integer quantity;

    /**
     * Only set for DISPENSE movements
     */
    private String patientId;
    private String patientType;
    private Long visitId;             // nullable — links a dispense back to the visit it happened during

    private String recordedBy;
    private LocalDateTime dateTime;
}