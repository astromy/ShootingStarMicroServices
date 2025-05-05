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
@Document(value = "staff_designation")
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class StaffDesignation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String institutionCode;

    @DBRef
    @OneToMany(fetch = FetchType.EAGER,targetEntity =Portfolio.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffDesignationList",referencedColumnName = "id")
    private List<DesignationList> staffDesignationList;
}
