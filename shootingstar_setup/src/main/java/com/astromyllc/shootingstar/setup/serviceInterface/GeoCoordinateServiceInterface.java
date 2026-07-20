package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.SaveGeofenceBoundaryRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.GeoCoordinateResponse;

import java.util.List;
import java.util.Optional;

public interface GeoCoordinateServiceInterface {
    List<Optional<GeoCoordinateResponse>> addGeoCoordinates(SaveGeofenceBoundaryRequest request);

    List<Optional<GeoCoordinateResponse>> updateGeoCoordinates(SaveGeofenceBoundaryRequest request);

    List<Optional<GeoCoordinateResponse>> getGeoCoordinatesByInstitution(SingleStringRequest beceCode);
}