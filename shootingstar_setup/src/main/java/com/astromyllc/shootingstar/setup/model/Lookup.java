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
    private String name;
    private String type;
}
