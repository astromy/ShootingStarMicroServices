package com.astromyllc.shootingstar.accommodation.dto.request;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public class BlockRoomRequest {
    private Long idBlockRoom;
    private String name;
    private Integer reservedBeds;
    private Integer generalBeds;
}
