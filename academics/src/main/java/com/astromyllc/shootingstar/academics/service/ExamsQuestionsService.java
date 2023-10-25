package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.ExamsQuestions;
import com.astromyllc.shootingstar.academics.repository.ExamsQuestionsRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsQuestionsServiceInterface;
import com.astromyllc.shootingstar.academics.util.ExamsQuestionsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamsQuestionsService implements ExamsQuestionsServiceInterface {

    private final ExamsQuestionsRepository examsQuestionsRepository;
    private final ExamsQuestionsUtil examsQuestionsUtil;
    @Override
    public ExamsQuestionsResponse submitQuestion(ExamsQuestionsRequest examsQuestionsRequest) {
      ExamsQuestions eq= examsQuestionsUtil.mapExamsRestionRequest_ToExamsQuestions(examsQuestionsRequest);
        examsQuestionsRepository.save(eq);
        examsQuestionsUtil.examsQuestionsGlobalList.add(eq);
        return examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq);
    }

    @Override
    public List<ExamsQuestionsResponse> submitQuestions(List<ExamsQuestionsRequest> examsQuestionsRequest) {
        List<ExamsQuestions> eq= examsQuestionsRequest.stream().map(eqr-> examsQuestionsUtil.mapExamsRestionRequest_ToExamsQuestions(eqr)).collect(Collectors.toList());
        examsQuestionsRepository.saveAll(eq);
        examsQuestionsUtil.examsQuestionsGlobalList.addAll(eq);
        return eq.stream().map(eqr->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eqr)).collect(Collectors.toList());
    }
}
