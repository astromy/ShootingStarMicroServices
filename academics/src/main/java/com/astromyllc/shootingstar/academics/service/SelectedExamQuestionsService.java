package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;
import com.astromyllc.shootingstar.academics.repository.SelectedExamsQuestionsRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.SelectedExamQuestionsServiceInterface;
import com.astromyllc.shootingstar.academics.util.SelectedExamQuestionsUtil;
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
public class SelectedExamQuestionsService implements SelectedExamQuestionsServiceInterface {
    private final SelectedExamQuestionsUtil selectedExamQuestionsUtil;
    private final SelectedExamsQuestionsRepository selectedExamsQuestionsRepository;

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return selectedExamQuestionsUtil.selectedExamQuestionsGlobalList.stream().filter(se->se.getClassId().equals(examsQuestionsRequest.getClassId()) && se.getTerm().equals(examsQuestionsRequest.getTerm())).map(s->selectedExamQuestionsUtil.mapSelectedExamsQuestion_ToSelectedExamsQuestionResponse(s)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByClass(ExamsQuestionsRequest examsQuestionsRequest) {
        return null;
    }

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClassAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return null;
    }

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClass(ExamsQuestionsRequest examsQuestionsRequest) {
        return null;
    }

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClassAndSubj(ExamsQuestionsRequest examsQuestionsRequest) {
        return null;
    }

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByInstAndClassAndSubjAndTerm(ExamsQuestionsRequest examsQuestionsRequest) {
        return null;
    }

    @Override
    public List<Optional<SelectedExamQuestionsResponse>> fetchSelectedQuestionsByClassAndSuj(ExamsQuestionsRequest examsQuestionsRequest) {
        return null;
    }
}
