package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.*;

import java.util.List;

public class SelectedExamQuestionsResponse {
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String term;
    private String classId;
    private String institutionCode;

    private List<SelectedExamQuestionAnswersResponse> selectedExamQuestionAnswersResponses;
}
