package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequest;
import com.astromyllc.shootingstar.setup.dto.response.DesignationResponse;

import java.util.List;
import java.util.Optional;

public interface DesignationServiceInterface {
    List<Optional<DesignationResponse>> createDesignation(DesignationRequest designationRequest);

    List<List<Optional<DesignationResponse>>> getAllDesignationByInstitution(DesignationRequest designationRequest);
}
