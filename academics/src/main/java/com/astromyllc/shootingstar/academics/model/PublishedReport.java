package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// One row per published set of results: a class's results for one term of one academic year, in one
// school. Parents see a student's results in the app only once their class/term/year is in here.
@Entity
@Table(name = "published_report")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class PublishedReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String institutionCode;
    private String studentClass;
    private String term;
    private String academicYear;
    private LocalDateTime publishedAt;
}