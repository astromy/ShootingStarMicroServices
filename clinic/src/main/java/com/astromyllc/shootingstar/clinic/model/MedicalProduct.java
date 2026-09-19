package com.astromyllc.shootingstar.clinic.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A medical supply/product held by the school clinic — was previously a
 * near-empty skeleton (just id + institutionCode). Fleshed out to actually
 * support a real pharmacy/stock workflow, mirroring the shape of
 * stores-inventory's StoreItem (name, unit, totalStock/availableStock,
 * reorder threshold, active flag).
 */
@Entity
@Table(name = "medicalproduct")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class MedicalProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institutionCode;
    private String productCode;      // human-scannable/internal code, unique per institution
    private String name;
    private String category;         // MEDICATION | CONSUMABLE | EQUIPMENT | OTHER
    private String unit;              // e.g. "tablet", "bottle", "box"

    private Integer totalStock;
    private Integer availableStock;
    private Integer reorderLevel;     // triggers a low-stock flag on the response, same idea as stores' low-stock report

    private Boolean active;
}