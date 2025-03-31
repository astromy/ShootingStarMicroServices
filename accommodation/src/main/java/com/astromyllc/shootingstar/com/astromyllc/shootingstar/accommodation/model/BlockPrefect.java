package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "block_prefect")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "idBlockPrefect")
public class BlockPrefect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBlockPrefect;
    private String studentName;
    @NonNull
    private String studentId;
    @NonNull
    private LocalDate appointmentDate;
    private LocalDate exitDate;
}
