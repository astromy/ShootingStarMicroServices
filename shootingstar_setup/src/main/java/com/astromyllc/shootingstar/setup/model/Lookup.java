package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lookup")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Lookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLookup;
    @NonNull
    private String name;
    @NonNull
    private String type;
}
