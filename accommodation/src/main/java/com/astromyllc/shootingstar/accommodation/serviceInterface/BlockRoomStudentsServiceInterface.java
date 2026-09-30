package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomStudentsRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockRoomStudentsResponse;

import java.util.List;

public interface BlockRoomStudentsServiceInterface {
    // Throws IllegalStateException if the room is already at capacity
    // (reservedBeds + generalBeds), or if the student already has an active
    // room assignment at this institution.
    BlockRoomStudentsResponse assignStudentToRoom(BlockRoomStudentsRequest request);

    boolean unassignStudent(Long idBlockRoomStudent);

    List<BlockRoomStudentsResponse> getRoomOccupants(Long idBlockRoom);

    List<BlockRoomStudentsResponse> getInstitutionAssignments(String institutionID);
}
