package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.SaveGeofenceBoundaryRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.GeoCoordinateResponse;
import com.astromyllc.shootingstar.setup.dto.response.GeofenceBoundaryResponse;
import com.astromyllc.shootingstar.setup.model.GeoCoordinate;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.GeoCoordinateRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.GeoCoordinateServiceInterface;
import com.astromyllc.shootingstar.setup.subscription.SubscriptionPlan;
import com.astromyllc.shootingstar.setup.utils.GeoCoordinateUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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
        String campusName = GeoCoordinateUtil.normalizeCampusName(request.getCampusName());

        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(request.getInstitution()))
                .findFirst()
                .map(inst -> {
                    assertCanAddCampus(inst, campusName);

                    inst.setGeoCoordinateList(
                            new ArrayList<>(Optional.ofNullable(inst.getGeoCoordinateList()).orElse(new ArrayList<>()))
                    );

                    List<GeoCoordinate> newCoordinates = request.getBoundary().stream()
                            .map(gp -> {
                                GeoCoordinate gc = GeoCoordinateUtil.mapGeoPoint_ToGeoCoordinate(gp, campusName);
                                gc.setInstitution(inst);
                                return gc;
                            })
                            .toList();

                    geoCoordinateRepository.saveAll(newCoordinates);
                    inst.getGeoCoordinateList().addAll(newCoordinates);

                    return inst.getGeoCoordinateList().stream()
                            .filter(gc -> campusName.equalsIgnoreCase(gc.getCampusName()))
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
        String campusName = GeoCoordinateUtil.normalizeCampusName(request.getCampusName());

        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(request.getInstitution()))
                .findFirst()
                .map(inst -> {
                    assertCanAddCampus(inst, campusName);

                    // 1) Remove this campus's old points in the database - including
                    //    legacy rows with a NULL campus name and any duplicates.
                    //    Other campuses' points are untouched.
                    int removed = geoCoordinateRepository.deleteCampusPoints(inst.getIdInstitution(), campusName);

                    // 2) Save the new ring. persist() assigns ids to these same objects,
                    //    so the in-memory copies below carry real ids.
                    List<GeoCoordinate> newCoordinates = request.getBoundary().stream()
                            .map(gp -> {
                                GeoCoordinate gc = GeoCoordinateUtil.mapGeoPoint_ToGeoCoordinate(gp, campusName);
                                gc.setInstitution(inst);
                                return gc;
                            })
                            .toList();
                    geoCoordinateRepository.saveAll(newCoordinates);

                    // 3) Mirror the same change in the in-memory list.
                    List<GeoCoordinate> updated = new ArrayList<>(
                            Optional.ofNullable(inst.getGeoCoordinateList()).orElse(new ArrayList<>()));
                    updated.removeIf(gc -> campusName.equalsIgnoreCase(
                            GeoCoordinateUtil.normalizeCampusName(gc.getCampusName())));
                    updated.addAll(newCoordinates);
                    inst.setGeoCoordinateList(updated);

                    log.info("Geofence for {} / {} replaced: {} old points removed, {} saved",
                            inst.getBececode(), campusName, removed, newCoordinates.size());

                    return newCoordinates.stream()
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

    /**
     * Groups an institution's points by campus - what "see all the fences" actually needs.
     */
    @Override
    public GeofenceBoundaryResponse getGeofenceBoundariesByInstitution(String beceCode) {
        List<GeoCoordinate> all = InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(beceCode))
                .findFirst()
                .map(Institution::getGeoCoordinateList)
                .orElse(Collections.emptyList());

        Map<String, List<GeoCoordinate>> byCampus = all.stream()
                .collect(Collectors.groupingBy(
                        gc -> GeoCoordinateUtil.normalizeCampusName(gc.getCampusName()),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<GeofenceBoundaryResponse.CampusBoundary> campuses = byCampus.entrySet().stream()
                .map(e -> GeofenceBoundaryResponse.CampusBoundary.builder()
                        .campusName(e.getKey())
                        .boundary(e.getValue().stream()
                                .map(GeoCoordinateUtil::mapGeoCoordinate_ToGeoCoordinateResponse)
                                .map(Optional::orElseThrow)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return GeofenceBoundaryResponse.builder().campuses(campuses).build();
    }

    @Override
    public List<String> getCampusNames(String beceCode) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(beceCode))
                .findFirst()
                .map(Institution::getGeoCoordinateList)
                .orElse(Collections.emptyList())
                .stream()
                .map(gc -> GeoCoordinateUtil.normalizeCampusName(gc.getCampusName()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteCampus(String beceCode, String campusName) {
        String normalized = GeoCoordinateUtil.normalizeCampusName(campusName);

        return InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(beceCode))
                .findFirst()
                .map(inst -> {
                    int removed = geoCoordinateRepository.deleteCampusPoints(inst.getIdInstitution(), normalized);

                    if (inst.getGeoCoordinateList() != null) {
                        List<GeoCoordinate> remaining = new ArrayList<>(inst.getGeoCoordinateList());
                        remaining.removeIf(gc -> normalized.equalsIgnoreCase(
                                GeoCoordinateUtil.normalizeCampusName(gc.getCampusName())));
                        inst.setGeoCoordinateList(remaining);
                    }
                    return removed > 0;
                })
                .orElse(false);
    }
    
    /**
     * Enforces "Growth gets one fence, Enterprise gets multiple" - the actual
     * mechanism behind the Enterprise-tier "multi-campus" feature. Adding a
     * point to a campus name that doesn't exist yet for this institution, while
     * the institution already has at least one OTHER campus, is what "adding a
     * new campus" means here. That's only allowed on Enterprise. Editing an
     * existing campus's boundary (same name) is always fine on any plan -
     * this only blocks growing the number of distinct campuses beyond one.
     */
    private void assertCanAddCampus(Institution institution, String campusName) {
        List<String> existingCampuses = Optional.ofNullable(institution.getGeoCoordinateList())
                .orElse(Collections.emptyList())
                .stream()
                .map(gc -> GeoCoordinateUtil.normalizeCampusName(gc.getCampusName()))
                .distinct()
                .toList();

        boolean isNewCampus = !existingCampuses.isEmpty()
                && existingCampuses.stream().noneMatch(c -> c.equalsIgnoreCase(campusName));

        if (!isNewCampus) {
            return;
        }

        SubscriptionPlan plan = SubscriptionPlan.fromLabel(institution.getSubscription());
        if (plan != SubscriptionPlan.ENTERPRISE) {
            throw new IllegalArgumentException(
                    "Institution " + institution.getBececode() + " is on " + plan
                            + " and already has a campus fence (" + existingCampuses + "). "
                            + "Multiple campuses require the Enterprise plan.");
        }
    }
}
