package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "applicationcategory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/*@Embeddable*/
@EqualsAndHashCode(of = "idApplicationCategory")
public class ApplicationCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idApplicationCategory;
   
    private BigDecimal applicationFormAmount;
    private String applicationFormType;
    private int applicationFormQNT;
    private String paymentMedium;
    private LocalDateTime commencement;
    private LocalDateTime closure;
    private int appointmentPerDay;
    private LocalDateTime appointmentCommencement;
    private LocalDateTime appointmentClosure;
/*
    @ManyToOne(optional = false)
    private Admissions admissions;*/
}
