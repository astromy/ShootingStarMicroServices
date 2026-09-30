package com.astromyllc.shootingstar.accommodation.dto.request;


import lombok.*;

import java.util.List;

// JPA relationship annotations (@OneToMany, @JoinColumn) were previously
// present here - those belong on the entity (Block.java), not on a plain
// request DTO. Spring silently ignores them here either way since this
// class was never registered as a JPA entity, but they didn't belong and
// only added confusion about which class actually owns the mapping.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockRequest {
    private Long idBlock;
    private String institutionCode;
    private String name;
    private String slogan;
    private String gender;

    @ToString.Exclude
    private List<BlockRoomRequest> roomsList;

}
