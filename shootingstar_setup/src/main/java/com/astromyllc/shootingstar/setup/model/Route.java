package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Route {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRoute;

    @NonNull
    private String name; // e.g. "Accra Route"

    private String description;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}