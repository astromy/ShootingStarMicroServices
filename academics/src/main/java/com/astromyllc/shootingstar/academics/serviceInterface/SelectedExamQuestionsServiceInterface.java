package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;

import java.util.List;
import java.util.Optional;

public interface SelectedExamQuestionsServiceInterface {
    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByClass(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClass(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClassAndSubj(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClassAndSubjAndTerm(ExamsQuestionsRequest examsQuestionsRequest);

    List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByClassAndSuj(ExamsQuestionsRequest examsQuestionsRequest);



}
