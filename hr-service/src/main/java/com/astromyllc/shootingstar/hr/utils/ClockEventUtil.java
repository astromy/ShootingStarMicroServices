package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.StaffClockInRequest;
import com.astromyllc.shootingstar.hr.dto.response.ClockEventResponse;
import com.astromyllc.shootingstar.hr.model.ClockEvent;

import java.time.Instant;

public class ClockEventUtil {

    public static ClockEvent mapRequest_ToClockEvent(StaffClockInRequest request) {
        return ClockEvent.builder()
                .staffCode(request.getStaffId())
                .institutionCode(request.getInstitutionCode())
                .type(request.getType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(Instant.now())
                .build();
    }

    public static ClockEventResponse mapClockEvent_ToClockEventResponse(ClockEvent event) {
        return ClockEventResponse.builder()
                .staffCode(event.getStaffCode())
                .institutionCode(event.getInstitutionCode())
                .type(event.getType())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .timestamp(event.getTimestamp())
                .build();
    }
}