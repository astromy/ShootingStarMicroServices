package com.astromyllc.shootingstar.accommodation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockRoomStudentsRequest {
    private Long idBlockRoomStudent;
    private String studentID;
    private String institutionID;
    // The service resolves the actual BlockRoom by ID rather than expecting
    // a full nested object here - this just needs to say which room.
    private Long idBlockRoom;
}
