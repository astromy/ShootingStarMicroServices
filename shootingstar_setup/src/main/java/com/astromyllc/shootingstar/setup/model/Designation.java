package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "designation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDesignation;
    @NonNull
    private String name;
    @NonNull
    private String code;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =JobDescription.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "designationJobDescription",referencedColumnName = "idDesignation")
    private List<JobDescription> jobDescriptionList;
}
