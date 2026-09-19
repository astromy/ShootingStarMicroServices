package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.SaveGeofenceBoundaryRequest;
import com.astromyllc.shootingstar.setup.dto.response.GeoCoordinateResponse;
import com.astromyllc.shootingstar.setup.model.GeoCoordinate;

import java.util.Optional;

public class GeoCoordinateUtil {

    public static final String DEFAULT_CAMPUS_NAME = "Main Campus";

    /** Blank/missing campus name defaults to "Main Campus" - keeps existing single-campus callers working unchanged. */
    public static String normalizeCampusName(String campusName) {
        return (campusName == null || campusName.isBlank()) ? DEFAULT_CAMPUS_NAME : campusName.trim();
    }

    public static GeoCoordinate mapGeoPoint_ToGeoCoordinate(SaveGeofenceBoundaryRequest.GeoPoint gp, String campusName) {
        return GeoCoordinate.builder()
                .latitude(gp.getLatitude())
                .longitude(gp.getLongitude())
                .campusName(normalizeCampusName(campusName))
                .build();
    }

    public static Optional<GeoCoordinateResponse> mapGeoCoordinate_ToGeoCoordinateResponse(GeoCoordinate g) {
        return Optional.ofNullable(GeoCoordinateResponse.builder()
                .idGeoCoordinate(g.getIdGeoCoordinate())
                .latitude(g.getLatitude())
                .longitude(g.getLongitude())
                .campusName(g.getCampusName())
                .build());
    }
}
