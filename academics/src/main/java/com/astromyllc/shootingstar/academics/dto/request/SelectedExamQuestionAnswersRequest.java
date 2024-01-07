package com.astromyllc.shootingstar.academics.dto.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class SelectedExamQuestionAnswersRequest {
    private Long id;
    private String answer;
    private boolean isQuestionAnswer;

}
