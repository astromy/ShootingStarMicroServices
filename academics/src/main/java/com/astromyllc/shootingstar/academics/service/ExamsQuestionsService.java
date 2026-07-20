package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.ExamsQuestions;
import com.astromyllc.shootingstar.academics.repository.ExamsQuestionsRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsQuestionsServiceInterface;
import com.astromyllc.shootingstar.academics.util.ExamsAnswersUtil;
import com.astromyllc.shootingstar.academics.util.ExamsQuestionsUtil;
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
public class ExamsQuestionsService implements ExamsQuestionsServiceInterface {

    private final ExamsQuestionsRepository examsQuestionsRepository;
    private final ExamsQuestionsUtil examsQuestionsUtil;

    @Override
    public Optional<ExamsQuestionsResponse> submitQuestion(ExamsQuestionsRequest examsQuestionsRequest) {
        ExamsQuestions eq = examsQuestionsUtil.mapExamsRestionRequest_ToExamsQuestions(examsQuestionsRequest);
        examsQuestionsRepository.save(eq);
        ExamsQuestionsUtil.examsQuestionsGlobalList.add(eq);
        return examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(eq);
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> submitQuestions(List<ExamsQuestionsRequest> examsQuestionsRequest) {
        List<ExamsQuestions> eq = examsQuestionsRequest.stream()
                .map(examsQuestionsUtil::mapExamsRestionRequest_ToExamsQuestions).toList();
        examsQuestionsRepository.saveAll(eq);
        ExamsQuestionsUtil.examsQuestionsGlobalList.addAll(eq);
        return eq.stream().map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public Optional<ExamsQuestionsResponse> updateQuestion(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getId().equals(examsQuestionsRequest.getId()))
                .findFirst()
                .map(existing -> {
                    // Update fields
                    existing.setQuestionDetail(examsQuestionsRequest.getQuestionDetail());
                    existing.setSubjectId(examsQuestionsRequest.getSubjectId());
                    existing.setClassId(examsQuestionsRequest.getClassId());
                    existing.setTerm(examsQuestionsRequest.getTerm());
                    existing.setInstitutionCode(examsQuestionsRequest.getInstitutionCode());
                    // Replace answers list
                    existing.setExamsAnswers(
                            examsQuestionsRequest.getExamsAnswersRequests().stream()
                                    .map(ExamsAnswersUtil::mapAnswerRequestToExamsAnswers)
                                    .toList()
                    );
                    examsQuestionsRepository.save(existing);
                    return examsQuestionsUtil.mapExamsQuestions_ToExamsQuestionResponse(existing)
                            .orElse(null);
                });
    }

    @Override
    public void deleteQuestion(ExamsQuestionsRequest examsQuestionsRequest) {
        ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getId().equals(examsQuestionsRequest.getId()))
                .findFirst()
                .ifPresent(existing -> {
                    examsQuestionsRepository.delete(existing);
                    ExamsQuestionsUtil.examsQuestionsGlobalList.remove(existing);
                    log.info("Deleted ExamsQuestion with id {}", existing.getId());
                });
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                        && e.getTerm().equalsIgnoreCase(examsQuestionsRequest.getTerm()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClass(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                        && e.getTerm().equalsIgnoreCase(examsQuestionsRequest.getTerm())
                        && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClass(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                        && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndSubj(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                        && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode())
                        && e.getSubjectId().equalsIgnoreCase(examsQuestionsRequest.getSubjectId()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByInstAndClassAndSubjAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                        && e.getTerm().equalsIgnoreCase(examsQuestionsRequest.getTerm())
                        && e.getSubjectId().equalsIgnoreCase(examsQuestionsRequest.getSubjectId())
                        && e.getInstitutionCode().equalsIgnoreCase(examsQuestionsRequest.getInstitutionCode()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }

    @Override
    public List<Optional<ExamsQuestionsResponse>> fetchQuestionsByClassAndSuj(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestionsUtil.examsQuestionsGlobalList.stream()
                .filter(e -> e.getClassId().equalsIgnoreCase(examsQuestionsRequest.getClassId())
                        && e.getSubjectId().equalsIgnoreCase(examsQuestionsRequest.getSubjectId()))
                .map(examsQuestionsUtil::mapExamsQuestions_ToExamsQuestionResponse).toList();
    }
}