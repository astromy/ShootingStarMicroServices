package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subject {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubject;
    @NonNull
    private String name;
    @NonNull
    private String classGroup;
    @NonNull
    private Integer preference;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}
