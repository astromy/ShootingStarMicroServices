package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jobdescription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
@EqualsAndHashCode(of = "idJobDescription")
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJobDescription;
    private String jobDescription;
}
