package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(value = "portfolio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String staffPortfolio;
}
