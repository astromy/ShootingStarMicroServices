package com.astromyllc.shootingstar.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "grading")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "idGrading")
public class Grading {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrading;
 @NonNull
    private Double lowerLimit;
 @NonNull
    private String grade;
    private String comment;
    //@ManyToOne(optional = false)
   // private GradingSetting gradingSetting;
}
