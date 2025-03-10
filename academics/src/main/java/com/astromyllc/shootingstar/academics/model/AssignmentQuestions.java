package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "assignment_question")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class AssignmentQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String classId;
    private String term;
    private String institutionCode;


    @OneToMany(fetch = FetchType.EAGER,targetEntity =AssignmentAnswers.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "assignmentQuestionAns",referencedColumnName = "id")
    private List<AssignmentAnswers> assignmentAnswers;
}
