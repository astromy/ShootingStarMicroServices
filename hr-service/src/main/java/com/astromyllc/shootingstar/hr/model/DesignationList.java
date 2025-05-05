package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Entity
@Document(value = "designationList")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
@EqualsAndHashCode(of = "id")
public class DesignationList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String department;
    private String designation;
    private String institutionCode;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Portfolio.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "designationUnits",referencedColumnName = "id")
    private List<DesignationUnit> designationUnits;
}
