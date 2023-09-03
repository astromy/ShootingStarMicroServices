package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class SelectedAssignmentQuestionAnswersResponse {
    private Long id;
    private String answer;
    private boolean isAnswer;

}
