package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "selected_assignment_question_answer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SelectedAssignmentQuestionAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answer;
    private boolean isAnswer;

}
