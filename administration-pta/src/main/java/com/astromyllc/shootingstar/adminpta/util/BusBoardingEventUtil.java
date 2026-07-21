package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.BusBoardingRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.BusBoardingResponse;
import com.astromyllc.shootingstar.adminpta.model.BusBoardingEvent;

import java.time.Instant;

public class BusBoardingEventUtil {

    public static BusBoardingEvent mapRequest_ToBusBoardingEvent(BusBoardingRequest request) {
        return BusBoardingEvent.builder()
                .studentId(request.getStudentId())
                .institutionCode(request.getInstitutionCode())
                .busId(request.getBusId())
                .busName(request.getBusName())
                .recordedBy(request.getRecordedBy())
                .type(request.getType())
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
                .timestamp(event.getTimestamp())
                .build();
    }
}