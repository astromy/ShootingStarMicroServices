package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.BusBoardingRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.BusBoardingResponse;
import com.astromyllc.shootingstar.adminpta.model.BusBoardingEvent;

import java.time.Instant;
import java.util.Objects;

public class BusBoardingEventUtil {

    public static boolean isRouteMismatch(Long studentAssignedRouteId, Long busRouteId) {
        return studentAssignedRouteId != null && !Objects.equals(studentAssignedRouteId, busRouteId);
    }

    public static BusBoardingEvent mapRequest_ToBusBoardingEvent(BusBoardingRequest request, boolean routeMismatch, String expectedRouteName) {
        return BusBoardingEvent.builder()
                .studentId(request.getStudentId())
                .institutionCode(request.getInstitutionCode())
                .busId(request.getBusId())
                .busName(request.getBusName())
                .routeId(request.getRouteId())
                .routeName(request.getRouteName())
                .recordedBy(request.getRecordedBy())
                .type(request.getType())
                .routeMismatch(routeMismatch)
                .expectedRouteName(expectedRouteName)
                .timestamp(Instant.now())
                .build();
    }

    public static BusBoardingResponse mapBusBoardingEvent_ToBusBoardingResponse(BusBoardingEvent event) {
        return BusBoardingResponse.builder()
                .studentId(event.getStudentId())
                .institutionCode(event.getInstitutionCode())
                .busId(event.getBusId())
                .busName(event.getBusName())
                .recordedBy(event.getRecordedBy())
                .type(event.getType())
                .routeMismatch(event.isRouteMismatch())
                .expectedRouteName(event.getExpectedRouteName())
                .timestamp(event.getTimestamp())
                .build();
    }
}