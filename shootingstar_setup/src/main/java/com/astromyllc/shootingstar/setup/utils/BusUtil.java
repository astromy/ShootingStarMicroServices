package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.BusDetails;
import com.astromyllc.shootingstar.setup.dto.response.BusResponse;
import com.astromyllc.shootingstar.setup.model.Bus;
import com.astromyllc.shootingstar.setup.repository.BusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BusUtil {

    public static List<Bus> busGlobalList = null;
    private final BusRepository busRepository;

    public static Bus mapBusRequest_ToBus(BusDetails d) {
        return Bus.builder()
                .name(d.getName())
                .plateNumber(d.getPlateNumber())
                .build();
    }

    public static Optional<BusResponse> mapBus_ToBusResponse(Bus b) {
        return Optional.ofNullable(BusResponse.builder()
                .idBus(b.getIdBus())
                .name(b.getName())
                .plateNumber(b.getPlateNumber())
                .build());
    }

    @PostConstruct
    private void fetchAllBus() {
        busGlobalList = busRepository.findAll();
        log.info("Global Bus List populated with {} records", (long) busGlobalList.size());
    }
}