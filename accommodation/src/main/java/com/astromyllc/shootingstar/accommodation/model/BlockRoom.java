package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "block_room")
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Same fix as BlockMaster/BlockPrefect - @Entity and @Embeddable were
// contradictory here.
@Data
@EqualsAndHashCode(of = "idBlockRoom")
public class BlockRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBlockRoom;
    @NonNull
    private String name;
    @NonNull
    private Integer reservedBeds;
    @NonNull
    private Integer generalBeds;
}
