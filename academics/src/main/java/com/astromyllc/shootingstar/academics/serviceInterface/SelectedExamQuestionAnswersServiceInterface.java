package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionAnswersResponse;

import java.util.List;
import java.util.Optional;

public interface SelectedExamQuestionAnswersServiceInterface {
    Optional<SelectedExamQuestionAnswersResponse> fetchAnsToQuestion(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionAnswersResponse>> fetchAnsToQuestionList(List<ExamsQuestionsRequest> examsQuestionsRequest);
}

