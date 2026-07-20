package com.astromyllc.shootingstar.academics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssignmentRequest {
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
    private String deadline;   // ISO string from frontend e.g. "2025-06-30T23:59"
    private String status;

    // Source question pool — teacher selects from question bank
    private List<Long> selectedQuestionIds;
}