package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grading")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
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
}
