package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "selected_exams_question_answers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class SelectedExamQuestionAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answer;
    private Boolean isQuestionAnswer;

}
