package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusDetails {
    // Present when updating an existing bus; null when adding a new one.
    private Long idBus;

    @NonNull
    private String name;

    private String registrationPlate;
    private String vehicleType;
    private String vehicleBrand;
    private Integer sittingCapacity;

    private LocalDate insuranceExpiryDate;
    private LocalDate roadworthyExpiryDate;

    private String driverStaffCode;
    private Long routeId;
}