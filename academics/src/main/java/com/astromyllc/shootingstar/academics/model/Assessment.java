package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "assessment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double classScore;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double examsScore;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double totalScore;
    private Integer position;
    private String subject;
    private String term;
    private String studentClass;
    private String academicYear;
    private String studentId;
    private String grade;
    private String gradeRemarks;
    private String institutionCode;
    @Column(name = "dateTime")
    private LocalDateTime dateTime;
}
