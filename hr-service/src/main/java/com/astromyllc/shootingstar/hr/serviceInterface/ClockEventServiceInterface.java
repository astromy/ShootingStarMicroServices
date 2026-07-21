package com.astromyllc.shootingstar.hr.serviceInterface;

import com.astromyllc.shootingstar.hr.config.StaffNotEligibleException;
import com.astromyllc.shootingstar.hr.dto.request.StaffClockInRequest;
import com.astromyllc.shootingstar.hr.dto.response.ClockEventResponse;

public interface ClockEventServiceInterface {
    ClockEventResponse recordClockEvent(StaffClockInRequest request) throws StaffNotEligibleException;
}