package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.BusBoardingRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.BusBoardingResponse;

public interface BusBoardingEventServiceInterface {
    // Throws StudentNotEligibleException (message safe to show the user) if
    // the studentId doesn't exist, or exists but doesn't belong to the
    // given institutionCode.
    BusBoardingResponse recordBusBoardingEvent(BusBoardingRequest request) throws StudentNotEligibleException;
}