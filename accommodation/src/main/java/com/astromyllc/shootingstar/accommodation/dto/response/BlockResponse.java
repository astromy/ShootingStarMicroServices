package com.astromyllc.shootingstar.accommodation.dto.response;


import lombok.*;

import java.util.List;

// Same cleanup as BlockRequest - stray JPA relationship annotations removed
// (they don't belong on a response DTO), and they also referenced the wrong
// type (BlockRoomRequest instead of BlockRoomResponse), which disappears
// along with them.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockResponse {
    private Long idBlock;
    private String institutionCode;
    private String name;
    private String slogan;
    private String gender;

    @ToString.Exclude
    private List<BlockRoomResponse> roomsList;
    private List<BlockMasterResponse> blockMasters;
    private List<BlockPrefectResponse> blockPrefects;

}
