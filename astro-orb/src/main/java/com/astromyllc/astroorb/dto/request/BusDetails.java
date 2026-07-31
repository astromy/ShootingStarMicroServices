package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusDetails {
    private Long idBus;
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