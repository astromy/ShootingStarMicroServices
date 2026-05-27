package com.astromyllc.shootingstar.accommodation.dto.response;


import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockResponse {
    private Long idBlock;
    private String name;
    private String slogan;
    private String gender;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, targetEntity = BlockRoomRequest.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "blockRoom", referencedColumnName = "idBlock")
    private List<BlockRoomResponse> roomsList;


}
