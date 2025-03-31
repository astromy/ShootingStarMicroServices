package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "block")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "idBlock")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBlock;
    @NonNull
    private String name;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,targetEntity =BlockRoom.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "blockRoom",referencedColumnName = "idBlock")
    private List<BlockRoom> roomsList;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,targetEntity =BlockMaster.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "blockMaster",referencedColumnName = "idBlock")
    private List<BlockMaster> blockMasters;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,targetEntity =BlockPrefect.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "blockPrefect",referencedColumnName = "idBlock")
    private List<BlockPrefect> blockPrefects;

}
