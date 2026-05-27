package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignment_answer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class AssignmentAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answer;
    private Boolean isQuestionAnswer;
}
