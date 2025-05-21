package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "gradingsetting")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GradingSetting {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGradingSetting;
    @Column(nullable = false)
    private Double classPercentage;
    @Column(nullable = false)
    private Double examsPercentage;
    private Double trailingMark;
    private int allowedTrails;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Grading.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gradeSettingGrading",referencedColumnName = "idGradingSetting")
    private List<Grading> gradingList;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}
