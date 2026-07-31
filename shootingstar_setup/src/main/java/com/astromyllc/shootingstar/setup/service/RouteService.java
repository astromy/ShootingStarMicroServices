package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.RouteRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.RouteResponse;
import com.astromyllc.shootingstar.setup.model.Route;
import com.astromyllc.shootingstar.setup.repository.RouteRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.RouteServiceInterface;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import com.astromyllc.shootingstar.setup.utils.RouteUtil;
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
public class RouteService implements RouteServiceInterface {
    private final RouteRepository routeRepository;

    @Override
    public List<Optional<RouteResponse>> createRoutes(RouteRequest routeRequest) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(routeRequest.getInstitution()))
                .findFirst()
                .map(inst -> {
                    inst.setRouteList(
                            new ArrayList<>(Optional.ofNullable(inst.getRouteList()).orElse(new ArrayList<>()))
                    );

                    List<Route> newRoutes = routeRequest.getRouteDetailsList().stream()
                            .map(d -> {
                                Route r = RouteUtil.mapRouteRequest_ToRoute(d);
                                r.setInstitution(inst);
                                return r;
                            })
                            .filter(r -> inst.getRouteList().stream().noneMatch(existing -> existing.getName().equalsIgnoreCase(r.getName())))
                            .toList();

                    routeRepository.saveAll(newRoutes);
                    inst.getRouteList().addAll(newRoutes);

                    return inst.getRouteList().stream()
                            .map(RouteUtil::mapRoute_ToRouteResponse)
                            .collect(Collectors.toList());
                })
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<Optional<RouteResponse>>();
                });
    }

    @Override
    public List<Optional<RouteResponse>> getRoutesByInstitution(SingleStringRequest beceCode) {
        String finalBeceCode = beceCode.getVal();
        return Optional.ofNullable(InstitutionUtils.institutionGlobalList)
                .flatMap(list -> list.stream()
                        .filter(i -> i.getBececode().equalsIgnoreCase(finalBeceCode))
                        .findFirst()
                        .flatMap(i -> Optional.ofNullable(i.getRouteList()))
                        .map(routeList -> routeList.stream()
                                .map(RouteUtil::mapRoute_ToRouteResponse)
                                .collect(Collectors.toList())))
                .orElse(Collections.emptyList());
    }
}