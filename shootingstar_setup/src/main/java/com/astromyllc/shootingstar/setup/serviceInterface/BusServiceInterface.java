package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.BusRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.BusResponse;

import java.util.List;
import java.util.Optional;

public interface BusServiceInterface {
    List<Optional<BusResponse>> createBuses(BusRequest busRequest);

    List<Optional<BusResponse>> getBusesByInstitution(SingleStringRequest beceCode);
}