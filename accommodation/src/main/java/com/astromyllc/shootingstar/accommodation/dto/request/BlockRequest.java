package com.astromyllc.shootingstar.accommodation.dto.request;


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
public class BlockRequest {
    private Long idBlock;
    private String name;
    private String slogan;
    private String gender;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, targetEntity = BlockRoomRequest.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "blockRoom", referencedColumnName = "idBlock")
    private List<BlockRoomRequest> roomsList;


}
