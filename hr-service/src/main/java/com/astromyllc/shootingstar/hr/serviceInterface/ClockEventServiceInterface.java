package com.astromyllc.shootingstar.hr.serviceInterface;

import com.astromyllc.shootingstar.hr.dto.request.StaffClockInRequest;
import com.astromyllc.shootingstar.hr.dto.response.ClockEventResponse;

import java.util.Optional;

public interface ClockEventServiceInterface {
    Optional<ClockEventResponse> recordClockEvent(StaffClockInRequest request);
}