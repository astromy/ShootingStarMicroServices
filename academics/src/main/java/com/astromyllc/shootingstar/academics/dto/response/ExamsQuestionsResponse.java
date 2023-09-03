package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.*;

import java.util.List;

public class ExamsQuestionsResponse {
    private Long id;
    private String questionDetail;
    private String subjectId;
    private String classId;
    private String term;
    private String institutionCode;

    private List<ExamsAnswersResponse> examsAnswersResponses;
}
