package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.CreateDutyRotationRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.UpdateDutyStatusRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.DutyAssignmentResponse;

import java.util.List;

public interface DutyAssignmentServiceInterface {
    // Generates one DutyAssignment row per week for the requested range -
    // this is what "recurring rotation" means at the data level.
    List<DutyAssignmentResponse> createRotation(CreateDutyRotationRequest request);

    DutyAssignmentResponse updateStatus(UpdateDutyStatusRequest request);

    List<DutyAssignmentResponse> getBlockRoster(Long idBlock);

    boolean cancelRotation(String rotationId);

    boolean deleteAssignment(Long idDutyAssignment);
}
