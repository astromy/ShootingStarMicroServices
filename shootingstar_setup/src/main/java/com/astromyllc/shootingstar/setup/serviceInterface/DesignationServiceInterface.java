package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.DesignationResponse;

import java.util.List;
import java.util.Optional;

public interface DesignationServiceInterface {
    Optional<List<Optional<DesignationResponse>>> createDesignation(DesignationRequest designationRequest);

    Optional<List<List<Optional<DesignationResponse>>>> getAllDesignationByInstitution(SingleStringRequest singleStringRequest);

    Optional<List<List<Optional<DesignationResponse>>>> getAllDesignationByInstitutionX(DesignationRequest designationRequest);
}
