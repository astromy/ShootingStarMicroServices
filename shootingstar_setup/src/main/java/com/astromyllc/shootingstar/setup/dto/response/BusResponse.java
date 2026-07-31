package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusResponse {
    private Long idBus;
    private String name;
    private String registrationPlate;
    private String vehicleType;
    private String vehicleBrand;
    private Integer sittingCapacity;

    private LocalDate insuranceExpiryDate;
    private LocalDate roadworthyExpiryDate;
    private String insuranceStatus;  // "VALID" | "EXPIRED"
    private String roadworthyStatus; // "VALID" | "EXPIRED"

    private String driverStaffCode;

    private Long routeId;
    private String routeName;
}