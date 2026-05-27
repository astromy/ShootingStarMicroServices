package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockResponse;

public interface BlockServiceInterface {
    BlockResponse addInstitutionAccommodation(BlockRequest blockRequest);

    BlockResponse getInstitutionAccommodation(SingleStringRequest request);
}
