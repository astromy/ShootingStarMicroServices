package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "selected_exams_questions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class SelectedExamQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String term;
    private String classId;
    private String institutionCode;

    @OneToMany(fetch = FetchType.EAGER,targetEntity =SelectedExamQuestionAnswers.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "selectExamQuestionToAnswer",referencedColumnName = "id")
    private List<SelectedExamQuestionAnswers> selectedExamQuestionAnswers;
}
