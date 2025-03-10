package com.astromyllc.shootingstar.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admissioncriteria")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "idAdmissionCriteria")
public class AdmissionCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdmissionCriteria;
    private String criteria;
    private int value;
    /*This tells how the criteria combines with each other(OR/And)*/
    private String operand;
}
