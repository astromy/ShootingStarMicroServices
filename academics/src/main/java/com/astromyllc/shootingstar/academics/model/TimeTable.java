package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timetable")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String subject;
    private String period;
    private String subjectClass;
    private String institutionCode;
}
