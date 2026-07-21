package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bus")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Bus {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBus;

    @NonNull
    private String name; // e.g. "Bus 3 - Accra Route"

    private String plateNumber;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}