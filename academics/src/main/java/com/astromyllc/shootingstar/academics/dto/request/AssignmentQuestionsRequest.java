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
public class AssignmentQuestionsRequest {
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String classId;
    private String term;
    private String institutionCode;

    private List<AssignmentAnswersRequest> assignmentAnswersRequests;
}
