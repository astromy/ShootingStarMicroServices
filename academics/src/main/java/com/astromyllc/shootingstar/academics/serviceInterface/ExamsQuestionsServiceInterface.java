package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;

import java.util.List;

public interface ExamsQuestionsServiceInterface {

    ExamsQuestionsResponse submitQuestion(ExamsQuestionsRequest examsQuestionsRequest);

    List<ExamsQuestionsResponse> submitQuestions(List<ExamsQuestionsRequest> examsQuestionsRequest);
}
