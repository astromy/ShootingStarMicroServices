package com.astromyllc.shootingstar.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
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
    private int preference;
    /*@ManyToOne(optional = false)
    private Institution institution;*/
}
