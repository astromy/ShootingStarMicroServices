package com.astromyllc.shootingstar.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "gradingsetting")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "idGradingSetting")
public class GradingSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGradingSetting;
    @NonNull
    private Double classPercentage;
    @NonNull
    private Double examsPercentage;
    private Double trailingMark;
    private int allowedTrails;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Grading.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "gradeSettingGrading",referencedColumnName = "idGradingSetting")
    private List<Grading> gradingList;
    /*@OneToOne(mappedBy = "gradingSetting",cascade = CascadeType.ALL)
    private Institution institution;*/
}
