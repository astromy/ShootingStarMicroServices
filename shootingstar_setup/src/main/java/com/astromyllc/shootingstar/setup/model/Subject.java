package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubject;
    @NonNull
    private String name;
    @NonNull
    private String classGroup;
    @NonNull
    private Integer preference;
    /*@ManyToOne(optional = false)
    private Institution institution;*/
}
