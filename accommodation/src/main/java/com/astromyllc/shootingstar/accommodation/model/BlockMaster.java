package com.astromyllc.shootingstar.accommodation.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "block_master")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
// @Entity and @Embeddable were both present here - contradictory JPA
// annotations. @Entity means this has its own identity and table, which is
// what the @OneToMany + @JoinColumn relationship on Block.blockMasters
// actually needs. @Embeddable means the opposite (a value type with no
// identity of its own, embedded directly into the owner's table) and isn't
// compatible with the standalone @Id/@GeneratedValue this class already has.
@EqualsAndHashCode(of = "idBlockMaster")
public class BlockMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBlockMaster;
    private String staffName;
    @NonNull
    private String staffId;
    @NonNull
    private LocalDate appointmentDate;
    private LocalDate exitDate;
}
