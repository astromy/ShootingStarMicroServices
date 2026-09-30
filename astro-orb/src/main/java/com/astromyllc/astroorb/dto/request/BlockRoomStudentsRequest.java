package com.astromyllc.astroorb.dto.request;

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
    // Matches the backend's request shape - just the room's ID, not a full
    // nested object the frontend wouldn't have fully populated anyway.
    private Long idBlockRoom;
}
