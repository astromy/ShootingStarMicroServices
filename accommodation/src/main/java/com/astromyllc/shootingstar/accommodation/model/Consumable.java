package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consumable")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Consumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsumable;

    @NonNull
    private String institutionCode;

    @NonNull
    private Long idBlock;

    @NonNull
    private String name;

    @NonNull
    private Integer quantity;

    private String unit;

    // IN_STOCK, LOW, DEPLETED - derived/settable independently of quantity
    // so staff can flag "low" by judgement, not just a fixed threshold.
    @Builder.Default
    private String stockStatus = "IN_STOCK";

    private LocalDate lastRestockedDate;
}
