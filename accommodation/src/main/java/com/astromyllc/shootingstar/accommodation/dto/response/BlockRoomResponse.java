package com.astromyllc.shootingstar.accommodation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockRoomResponse {
    private Long idBlockRoom;
    private String name;
    private Integer reservedBeds;
    private Integer generalBeds;
}
