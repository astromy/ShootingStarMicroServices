package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.GateCheckRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.GateEventResponse;
import com.astromyllc.shootingstar.adminpta.model.GateEvent;

import java.time.Instant;

public class GateEventUtil {

    public static GateEvent mapRequest_ToGateEvent(GateCheckRequest request) {
        return GateEvent.builder()
                .studentId(request.getStudentId())
                .institutionCode(request.getInstitutionCode())
                .recordedBy(request.getRecordedBy())
                .type(request.getType())
                .timestamp(Instant.now())
                .build();
    }

    public static GateEventResponse mapGateEvent_ToGateEventResponse(GateEvent event) {
        return GateEventResponse.builder()
                .studentId(event.getStudentId())
                .institutionCode(event.getInstitutionCode())
                .recordedBy(event.getRecordedBy())
                .type(event.getType())
                .timestamp(event.getTimestamp())
                .build();
    }
}
