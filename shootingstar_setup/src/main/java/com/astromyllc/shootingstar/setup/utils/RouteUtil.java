package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.RouteDetails;
import com.astromyllc.shootingstar.setup.dto.response.RouteResponse;
import com.astromyllc.shootingstar.setup.model.Route;
import com.astromyllc.shootingstar.setup.repository.RouteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteUtil {

    public static List<Route> routeGlobalList = null;
    private final RouteRepository routeRepository;

    public static Route mapRouteRequest_ToRoute(RouteDetails d) {
        return Route.builder()
                .name(d.getName())
                .description(d.getDescription())
                .build();
    }

    public static Optional<RouteResponse> mapRoute_ToRouteResponse(Route r) {
        return Optional.ofNullable(RouteResponse.builder()
                .idRoute(r.getIdRoute())
                .name(r.getName())
                .description(r.getDescription())
                .build());
    }

    @PostConstruct
    private void fetchAllRoutes() {
        routeGlobalList = routeRepository.findAll();
        log.info("Global Route List populated with {} records", (long) routeGlobalList.size());
    }
}