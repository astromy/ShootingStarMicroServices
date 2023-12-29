package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionAnswersResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.SelectedExamQuestionAnswersServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SelectedExamQuestionAnswersService implements SelectedExamQuestionAnswersServiceInterface {


    @Override
    public Optional<SelectedExamQuestionAnswersResponse> fetchAnsToQuestion(ExamsQuestionsRequest examsQuestionsRequest) {
        return Optional.empty();
    }

    @Override
    public List<Optional<SelectedExamQuestionAnswersResponse>> fetchAnsToQuestionList(List<ExamsQuestionsRequest> examsQuestionsRequest) {
        return null;
    }
}
