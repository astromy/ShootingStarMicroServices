package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "selected_assignment_question_answer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class SelectedAssignmentQuestionAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answer;
    private boolean isQuestionAnswer;

}
