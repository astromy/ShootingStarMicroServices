package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.SaveGeofenceBoundaryRequest;
import com.astromyllc.shootingstar.setup.dto.response.GeoCoordinateResponse;
import com.astromyllc.shootingstar.setup.model.GeoCoordinate;

import java.util.Optional;

public class GeoCoordinateUtil {

    public static GeoCoordinate mapGeoPoint_ToGeoCoordinate(SaveGeofenceBoundaryRequest.GeoPoint gp) {
        return GeoCoordinate.builder()
                .latitude(gp.getLatitude())
                .longitude(gp.getLongitude())
                .build();
    }

    public static Optional<GeoCoordinateResponse> mapGeoCoordinate_ToGeoCoordinateResponse(GeoCoordinate g) {
        return Optional.ofNullable(GeoCoordinateResponse.builder()
                .idGeoCoordinate(g.getIdGeoCoordinate())
                .latitude(g.getLatitude())
                .longitude(g.getLongitude())
                .build());
    }
}