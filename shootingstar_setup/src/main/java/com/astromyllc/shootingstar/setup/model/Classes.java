package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Classes {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClasses;
    private String name;
    private String classGroup;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}
