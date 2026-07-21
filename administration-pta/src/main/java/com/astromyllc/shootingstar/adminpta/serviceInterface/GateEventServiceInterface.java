package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.GateCheckRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.GateEventResponse;

public interface GateEventServiceInterface {
    // Throws StudentNotEligibleException (message safe to show the user) if
    // the studentId doesn't exist, or exists but doesn't belong to the
    // given institutionCode.
    GateEventResponse recordGateEvent(GateCheckRequest request) throws StudentNotEligibleException;
}
