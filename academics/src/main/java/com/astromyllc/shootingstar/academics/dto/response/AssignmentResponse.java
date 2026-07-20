package com.astromyllc.shootingstar.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssignmentResponse {
    private Long id;
    private String title;
    private String subjectId;
    private String classId;
    private String term;
    private String institutionCode;
    private String staffId;
    private String deliveryMode;
    private String selectionMode;
    private Integer questionCount;
    private String deadline;
    private String createdAt;
    private String status;
    private List<AssignmentQuestionsResponse> selectedQuestions;
}