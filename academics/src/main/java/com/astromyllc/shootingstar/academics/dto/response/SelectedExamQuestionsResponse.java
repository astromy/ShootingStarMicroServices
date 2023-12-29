package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectedExamQuestionsResponse {
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String term;
    private String classId;
    private String institutionCode;

    private List<Optional<SelectedExamQuestionAnswersResponse>> selectedExamQuestionAnswersResponses;
}
