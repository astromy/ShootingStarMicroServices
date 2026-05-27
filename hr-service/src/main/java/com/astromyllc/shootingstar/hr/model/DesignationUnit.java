package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Entity
@Document(value = "designationUnit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
@EqualsAndHashCode(of = "id")
public class DesignationUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String unitName;
    private String designation;
    private String institutionCode;
}
