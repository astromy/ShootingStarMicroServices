package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionAnswersResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;

import java.util.List;
import java.util.Optional;

public interface ExamsQuestionsServiceInterface {

    Optional<ExamsQuestionsResponse> submitQuestion(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> submitQuestions(List<ExamsQuestionsRequest> examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClass(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClass(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndSubj(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndSubjAndTerm(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClassAndSuj(ExamsQuestionsRequest examsQuestionsRequest);
}