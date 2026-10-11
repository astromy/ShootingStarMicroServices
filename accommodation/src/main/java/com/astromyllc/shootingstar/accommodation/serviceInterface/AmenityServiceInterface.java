package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.AmenityRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.AmenityResponse;

import java.util.List;

public interface AmenityServiceInterface {
    AmenityResponse addAmenity(AmenityRequest request);

    AmenityResponse updateAmenity(AmenityRequest request);

    List<AmenityResponse> getBlockAmenities(Long idBlock);

    boolean deleteAmenity(Long idAmenity);
}
