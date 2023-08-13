package com.astromyllc.shootingstar.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClasses;
    private String name;
    private String classGroup;
   /* @ManyToOne(optional = false)
    private Institution institution;*/
}
