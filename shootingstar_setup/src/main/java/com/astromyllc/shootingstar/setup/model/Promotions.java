package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Promotions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;
    String currentClass;
    String targetClass;
    String academicYear;
    LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "idInstitution")
    private Institution institution;
}
