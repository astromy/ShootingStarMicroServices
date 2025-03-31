package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "block_room")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
@EqualsAndHashCode(of = "idBlockRoom")
public class BlockRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBlockRoom;
    @NonNull
    private String name;
    @NonNull
    private Integer capacity;
}
