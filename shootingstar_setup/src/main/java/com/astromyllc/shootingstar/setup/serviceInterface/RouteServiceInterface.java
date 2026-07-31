package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.RouteRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.RouteResponse;

import java.util.List;
import java.util.Optional;

public interface RouteServiceInterface {
    List<Optional<RouteResponse>> createRoutes(RouteRequest routeRequest);

    List<Optional<RouteResponse>> getRoutesByInstitution(SingleStringRequest beceCode);
}