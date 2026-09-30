package com.astromyllc.astroorb.dto.request;


import lombok.*;

import java.util.List;

// Mirrors the accommodation service's own BlockRequest - institutionCode
// added (was missing, meaning ORB could never actually tell the backend
// which school's block this was for) and the stray JPA relationship
// annotations removed (they never belonged on a plain proxy DTO).
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
