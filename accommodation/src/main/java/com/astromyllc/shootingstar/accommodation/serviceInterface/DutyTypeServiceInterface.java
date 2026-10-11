package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.DutyTypeRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.DutyTypeResponse;

import java.util.List;

public interface DutyTypeServiceInterface {
    DutyTypeResponse addDutyType(DutyTypeRequest request);

    List<DutyTypeResponse> getDutyTypes(String institutionCode);

    boolean deleteDutyType(Long idDutyType);
}
