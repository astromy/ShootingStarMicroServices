package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Table(name = "lookup")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
@EqualsAndHashCode(of = "idLookup")
public class Lookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLookup;
    @NonNull
    @Unique
    private String name;
    @NonNull
    private String type;
}
