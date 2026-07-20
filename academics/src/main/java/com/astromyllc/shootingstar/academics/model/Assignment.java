package com.astromyllc.shootingstar.academics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assignment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subjectId;
    private String classId;
    private String term;
    private String institutionCode;
    private String staffId;

    // ONLINE or PRINT
    private String deliveryMode;

    // SAME_ORDER | SHUFFLED_ORDER | DIFFERENT_PER_STUDENT
    private String selectionMode;

    private Integer questionCount;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    // Status: DRAFT | PUBLISHED | CLOSED
    private String status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "assignmentQuestions", referencedColumnName = "id")
    private List<SelectedAssignmentQuestions> selectedQuestions;
}