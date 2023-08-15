package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "department")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartment;
    @NonNull
    private String name;
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Designation.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "departmentDesignation",referencedColumnName = "idDepartment")
    private List<Designation> designationList;
}
