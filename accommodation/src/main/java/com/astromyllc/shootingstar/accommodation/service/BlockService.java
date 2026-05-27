package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockResponse;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockServiceInterface;
import org.springframework.stereotype.Service;

@Service
public class BlockService implements BlockServiceInterface {
    @Override
    public BlockResponse getInstitutionAccommodation(SingleStringRequest blockRequest) {
        return null;
    }

    @Override
    public BlockResponse addInstitutionAccommodation(BlockRequest blockRequest) {
        return null;
    }
}
