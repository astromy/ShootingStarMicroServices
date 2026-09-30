package com.astromyllc.shootingstar.accommodation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockRoomStudentsResponse {
    private Long idBlockRoomStudent;
    private String studentID;
    private String institutionID;
    // Full room details included here (not just the ID) so a client can
    // display which room/block a student is in without a second lookup.
    private BlockRoomResponse blockRoom;
}
