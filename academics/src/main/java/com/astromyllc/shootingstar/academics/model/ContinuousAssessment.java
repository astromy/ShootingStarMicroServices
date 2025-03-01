package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "continuous_assessment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContinuousAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double score;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double totalScore;
    private Long subject;
    private String term;
    private String studentClass;
    private String academicYear;
    private String studentId;
    private LocalDateTime dateTime;
    private String institutionCode;
}
