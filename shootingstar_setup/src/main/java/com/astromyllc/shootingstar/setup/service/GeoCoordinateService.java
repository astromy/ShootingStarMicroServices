package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.SaveGeofenceBoundaryRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.GeoCoordinateResponse;
import com.astromyllc.shootingstar.setup.model.GeoCoordinate;
import com.astromyllc.shootingstar.setup.repository.GeoCoordinateRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.GeoCoordinateServiceInterface;
import com.astromyllc.shootingstar.setup.utils.GeoCoordinateUtil;
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
public class GeoCoordinateService implements GeoCoordinateServiceInterface {

    private final InstitutionRepository institutionRepository;
    private final GeoCoordinateRepository geoCoordinateRepository;

    @Override
    public List<Optional<GeoCoordinateResponse>> addGeoCoordinates(SaveGeofenceBoundaryRequest request) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(request.getInstitution()))
                .findFirst()
                .map(inst -> {
                    inst.setGeoCoordinateList(
                            new ArrayList<>(Optional.ofNullable(inst.getGeoCoordinateList()).orElse(new ArrayList<>()))
                    );

                    List<GeoCoordinate> newCoordinates = request.getBoundary().stream()
                            .map(gp -> {
                                GeoCoordinate gc = GeoCoordinateUtil.mapGeoPoint_ToGeoCoordinate(gp);
                                gc.setInstitution(inst);
                                return gc;
                            })
                            .toList();

                    geoCoordinateRepository.saveAll(newCoordinates);
                    inst.getGeoCoordinateList().addAll(newCoordinates);

                    return inst.getGeoCoordinateList().stream()
                            .map(GeoCoordinateUtil::mapGeoCoordinate_ToGeoCoordinateResponse)
                            .collect(Collectors.toList());
                })
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<>();
                });
    }

    @Override
    public List<Optional<GeoCoordinateResponse>> updateGeoCoordinates(SaveGeofenceBoundaryRequest request) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(request.getInstitution()))
                .findFirst()
                .map(inst -> {
                    if (inst.getGeoCoordinateList() == null) {
                        inst.setGeoCoordinateList(new ArrayList<>());
                    } else {
                        inst.getGeoCoordinateList().clear();
                    }

                    List<GeoCoordinate> newCoordinates = request.getBoundary().stream()
                            .map(gp -> {
                                GeoCoordinate gc = GeoCoordinateUtil.mapGeoPoint_ToGeoCoordinate(gp);
                                gc.setInstitution(inst);
                                return gc;
                            })
                            .toList();

                    inst.getGeoCoordinateList().addAll(newCoordinates);
                    institutionRepository.save(inst);

                    return inst.getGeoCoordinateList().stream()
                            .map(GeoCoordinateUtil::mapGeoCoordinate_ToGeoCoordinateResponse)
                            .collect(Collectors.toList());
                })
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<>();
                });
    }

    @Override
    public List<Optional<GeoCoordinateResponse>> getGeoCoordinatesByInstitution(SingleStringRequest beceCode) {
        String finalBeceCode = beceCode.getVal();
        return Optional.ofNullable(InstitutionUtils.institutionGlobalList)
                .flatMap(list -> list.stream()
                        .filter(i -> i.getBececode().equalsIgnoreCase(finalBeceCode))
                        .findFirst()
                        .flatMap(i -> Optional.ofNullable(i.getGeoCoordinateList()))
                        .map(coordList -> coordList.stream()
                                .map(GeoCoordinateUtil::mapGeoCoordinate_ToGeoCoordinateResponse)
                                .collect(Collectors.toList())))
                .orElse(Collections.emptyList());
    }
}