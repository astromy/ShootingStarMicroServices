package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionAnswersResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.ExamsQuestions;
import com.astromyllc.shootingstar.academics.repository.ExamsQuestionsRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsQuestionsServiceInterface;
import com.astromyllc.shootingstar.academics.util.ExamsQuestionsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamsQuestionsService implements ExamsQuestionsServiceInterface {

    private final ExamsQuestionsRepository examsQuestionsRepository;
    private final ExamsQuestionsUtil examsQuestionsUtil;
    @Override
    public Optional<ExamsQuestionsResponse> submitQuestion(ExamsQuestionsRequest examsQuestionsRequest) {
      ExamsQuestions eq= examsQuestionsUtil.mapExamsRestionRequest_ToExamsQuestions(examsQuestionsRequest);
        examsQuestionsRepository.save(eq);
        examsQuestionsUtil.examsQuestionsGlobalList.add(eq);
        return examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq);
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> submitQuestions(List<ExamsQuestionsRequest> examsQuestionsRequest) {
        List<ExamsQuestions> eq= examsQuestionsRequest.stream().map(eqr-> examsQuestionsUtil.mapExamsRestionRequest_ToExamsQuestions(eqr)).collect(Collectors.toList());
        examsQuestionsRepository.saveAll(eq);
        examsQuestionsUtil.examsQuestionsGlobalList.addAll(eq);
        return eq.stream().map(eqr->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eqr)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
       return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
        && e.getTerm().equalsIgnoreCase(examsQuestionsRequest.getTerm())).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClass(ExamsQuestionsRequest examsQuestionsRequest) {
        return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                && e.getTerm().equalsIgnoreCase(examsQuestionsRequest.getTerm())
                && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode())
        ).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClass(ExamsQuestionsRequest examsQuestionsRequest) {
        return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode())).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndSubj(ExamsQuestionsRequest examsQuestionsRequest) {
        return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode())
                && e.getSubjectId().equalsIgnoreCase(examsQuestionsRequest.getSubjectId())
        ).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());

    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndSubjAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                && e.getTerm().equalsIgnoreCase(examsQuestionsRequest.getTerm())
                && e.getSubjectId().equalsIgnoreCase(examsQuestionsRequest.getSubjectId())
                && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode())
        ).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());

    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClassAndSuj(ExamsQuestionsRequest examsQuestionsRequest) {
        return examsQuestionsUtil.examsQuestionsGlobalList.stream().filter(e->e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                && e.getSubjectId().equalsIgnoreCase(examsQuestionsRequest.getSubjectId())).map(eq->examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq)).collect(Collectors.toList());

    }

}
