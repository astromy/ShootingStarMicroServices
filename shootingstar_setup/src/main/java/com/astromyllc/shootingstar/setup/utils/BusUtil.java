package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.BusDetails;
import com.astromyllc.shootingstar.setup.dto.response.BusResponse;
import com.astromyllc.shootingstar.setup.model.Bus;
import com.astromyllc.shootingstar.setup.model.ComplianceStatus;
import com.astromyllc.shootingstar.setup.repository.BusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BusUtil {

    public static List<Bus> busGlobalList = null;
    private final BusRepository busRepository;

    // VALID/EXPIRED is always derived from the expiry date against today,
    // never taken directly from client input — a client can't set a bus to
    // "VALID" while its insurance date is in the past.
    public static ComplianceStatus computeStatus(LocalDate expiryDate) {
        if (expiryDate == null) {
            return null;
        }
        return expiryDate.isBefore(LocalDate.now()) ? ComplianceStatus.EXPIRED : ComplianceStatus.VALID;
    }

    public static Bus mapBusRequest_ToBus(BusDetails d) {
        return Bus.builder()
                .name(d.getName())
                .registrationPlate(d.getRegistrationPlate())
                .vehicleType(d.getVehicleType())
                .vehicleBrand(d.getVehicleBrand())
                .sittingCapacity(d.getSittingCapacity())
                .insuranceExpiryDate(d.getInsuranceExpiryDate())
                .roadworthyExpiryDate(d.getRoadworthyExpiryDate())
                .insuranceStatus(computeStatus(d.getInsuranceExpiryDate()))
                .roadworthyStatus(computeStatus(d.getRoadworthyExpiryDate()))
                .driverStaffCode(d.getDriverStaffCode())
                .build();
    }

    // Applies the editable fields from an update request onto an existing
    // Bus, recomputing compliance status from whatever expiry dates result.
    public static void applyUpdate(Bus bus, BusDetails d) {
        bus.setName(d.getName());
        bus.setRegistrationPlate(d.getRegistrationPlate());
        bus.setVehicleType(d.getVehicleType());
        bus.setVehicleBrand(d.getVehicleBrand());
        bus.setSittingCapacity(d.getSittingCapacity());
        bus.setInsuranceExpiryDate(d.getInsuranceExpiryDate());
        bus.setRoadworthyExpiryDate(d.getRoadworthyExpiryDate());
        bus.setInsuranceStatus(computeStatus(d.getInsuranceExpiryDate()));
        bus.setRoadworthyStatus(computeStatus(d.getRoadworthyExpiryDate()));
        bus.setDriverStaffCode(d.getDriverStaffCode());
    }

    public static Optional<BusResponse> mapBus_ToBusResponse(Bus b) {
        return Optional.ofNullable(BusResponse.builder()
                .idBus(b.getIdBus())
                .name(b.getName())
                .registrationPlate(b.getRegistrationPlate())
                .vehicleType(b.getVehicleType())
                .vehicleBrand(b.getVehicleBrand())
                .sittingCapacity(b.getSittingCapacity())
                .insuranceExpiryDate(b.getInsuranceExpiryDate())
                .roadworthyExpiryDate(b.getRoadworthyExpiryDate())
                .insuranceStatus(b.getInsuranceStatus() != null ? b.getInsuranceStatus().name() : null)
                .roadworthyStatus(b.getRoadworthyStatus() != null ? b.getRoadworthyStatus().name() : null)
                .driverStaffCode(b.getDriverStaffCode())
                .routeId(b.getRoute() != null ? b.getRoute().getIdRoute() : null)
                .routeName(b.getRoute() != null ? b.getRoute().getName() : null)
                .build());
    }

    @PostConstruct
    private void fetchAllBus() {
        busGlobalList = busRepository.findAll();
        log.info("Global Bus List populated with {} records", (long) busGlobalList.size());
    }
}