package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.BusRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.BusResponse;
import com.astromyllc.shootingstar.setup.model.Bus;
import com.astromyllc.shootingstar.setup.repository.BusRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.BusServiceInterface;
import com.astromyllc.shootingstar.setup.utils.BusUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusService implements BusServiceInterface {
    private final BusRepository busRepository;

    @Override
    public List<Optional<BusResponse>> createBuses(BusRequest busRequest) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(busRequest.getInstitution()))
                .findFirst()
                .map(inst -> {
                    inst.setBusList(
                            new ArrayList<>(Optional.ofNullable(inst.getBusList()).orElse(new ArrayList<>()))
                    );

                    List<Bus> newBuses = busRequest.getBusDetailsList().stream()
                            .map(d -> {
                                Bus b = BusUtil.mapBusRequest_ToBus(d);
                                b.setInstitution(inst);
                                return b;
                            })
                            .filter(b -> inst.getBusList().stream().noneMatch(existing -> existing.getName().equalsIgnoreCase(b.getName())))
                            .toList();

                    busRepository.saveAll(newBuses);
                    inst.getBusList().addAll(newBuses);

                    return inst.getBusList().stream()
                            .map(BusUtil::mapBus_ToBusResponse)
                            .collect(Collectors.toList());
                })
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<Optional<BusResponse>>();
                });
    }

    @Override
    public List<Optional<BusResponse>> getBusesByInstitution(SingleStringRequest beceCode) {
        String finalBeceCode = beceCode.getVal();
        return Optional.ofNullable(InstitutionUtils.institutionGlobalList)
                .flatMap(list -> list.stream()
                        .filter(i -> i.getBececode().equalsIgnoreCase(finalBeceCode))
                        .findFirst()
                        .flatMap(i -> Optional.ofNullable(i.getBusList()))
                        .map(busList -> busList.stream()
                                .map(BusUtil::mapBus_ToBusResponse)
                                .collect(Collectors.toList())))
                .orElse(Collections.emptyList());
    }
}