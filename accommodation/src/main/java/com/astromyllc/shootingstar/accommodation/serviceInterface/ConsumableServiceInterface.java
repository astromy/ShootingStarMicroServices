package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.ConsumableRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.ConsumableResponse;

import java.util.List;

public interface ConsumableServiceInterface {
    ConsumableResponse addConsumable(ConsumableRequest request);

    ConsumableResponse updateConsumable(ConsumableRequest request);

    List<ConsumableResponse> getBlockConsumables(Long idBlock);

    boolean deleteConsumable(Long idConsumable);
}
