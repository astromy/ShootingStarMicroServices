package com.astromyllc.shootingstar.academics.dto.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SelectedAssignmentQuestionsRequest {
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String term;
    private String classId;
    private String institutionCode;

    private List<SelectedAssignmentQuestionAnswersRequest> selectedAssignmentQuestionAnswersRequests;
}
