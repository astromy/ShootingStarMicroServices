package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "admissions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "idAdmissions")
public class Admissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdmissions;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =AdmissionCriteria.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "admissionAdmissionCriteria",referencedColumnName = "idAdmissions")
    private List<AdmissionCriteria> admissionCriteriaList;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =ApplicationCategory.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "admissionsApplicationCategory",referencedColumnName = "idAdmissions")
    private List<ApplicationCategory> applicationCategoryList;
}
