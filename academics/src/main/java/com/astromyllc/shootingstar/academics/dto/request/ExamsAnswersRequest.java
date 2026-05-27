package com.astromyllc.shootingstar.academics.dto.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExamsAnswersRequest {
    private Long id;
    private String answer;
    private Boolean isQuestionAnswer;
}
