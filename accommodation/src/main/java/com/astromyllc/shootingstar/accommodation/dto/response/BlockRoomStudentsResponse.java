package com.astromyllc.shootingstar.accommodation.dto.response;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "idBlockRoom")
    private BlockRoomResponse blockRoom;
}
