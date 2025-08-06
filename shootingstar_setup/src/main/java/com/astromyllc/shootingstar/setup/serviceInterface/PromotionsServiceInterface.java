package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.PromotionsRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.PromotionsResponse;

import java.util.List;
import java.util.Optional;

public interface PromotionsServiceInterface {
    public List<Optional<PromotionsResponse>> createPromotions(PromotionsRequest promotionsRequest);

    List<Optional<PromotionsResponse>> getInstitutionPromotionSetup(SingleStringRequest beceCode);
}
