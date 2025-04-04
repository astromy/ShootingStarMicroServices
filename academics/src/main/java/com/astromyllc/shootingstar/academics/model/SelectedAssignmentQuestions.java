package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "selected_assignment_questions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class SelectedAssignmentQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String term;
    private String classId;
    private String institutionCode;

    @OneToMany(fetch = FetchType.EAGER,targetEntity =SelectedAssignmentQuestionAnswers.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "selectedAssignmentQuestionAnswer",referencedColumnName = "id")
    private List<SelectedAssignmentQuestionAnswers> selectedAssignmentQuestionAnswers;
}
