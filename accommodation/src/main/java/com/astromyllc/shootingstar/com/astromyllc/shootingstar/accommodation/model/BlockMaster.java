package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "block_master")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
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
