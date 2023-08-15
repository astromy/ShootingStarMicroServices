package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String name;
    private String code;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =JobDescription.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "designationJobDescription",referencedColumnName = "idDesignation")
    private List<JobDescription> jobDescriptionList;
}
