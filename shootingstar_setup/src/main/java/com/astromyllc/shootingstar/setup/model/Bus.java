package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "bus")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Bus {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBus;

    @NonNull
    private String name; // internal label, e.g. "Bus 3"

    private String registrationPlate;
    private String vehicleType;   // e.g. "Coaster", "Minibus"
    private String vehicleBrand;  // e.g. "Toyota"
    private Integer sittingCapacity;

    private LocalDate insuranceExpiryDate;
    private LocalDate roadworthyExpiryDate;

    // Computed from the expiry dates above at save time and refreshed daily
    // by TransportComplianceScheduler — not directly settable by a client,
    // so it can't drift out of sync with the actual dates.
    @Enumerated(EnumType.STRING)
    private ComplianceStatus insuranceStatus;
    @Enumerated(EnumType.STRING)
    private ComplianceStatus roadworthyStatus;

    // Cross-service reference — the driver is a Staff record in the HR
    // microservice (different DB, different service), so this is a plain
    // staffCode string rather than a JPA relation. Resolve display details
    // (name, contact) via HR's getStaffByStaffCode when needed.
    private String driverStaffCode;

    @ManyToOne
    @JoinColumn(name = "idRoute")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}