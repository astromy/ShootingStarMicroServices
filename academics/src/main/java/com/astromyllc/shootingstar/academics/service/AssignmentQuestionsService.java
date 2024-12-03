package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.AssignmentQuestions;
import com.astromyllc.shootingstar.academics.model.ExamsQuestions;
import com.astromyllc.shootingstar.academics.repository.AssignmentQuestionRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.AssignmentQuestionsServiceInterface;
import com.astromyllc.shootingstar.academics.util.AssignmentQuestionsUtil;
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
public class AssignmentQuestionsService implements AssignmentQuestionsServiceInterface {
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final AssignmentQuestionsUtil assignmentQuestionsUtil;

    @Override
    public List<AssignmentQuestionsResponse> submitAssignmentQuestions(List<AssignmentQuestionsRequest> assignmentQuestionsRequest) {
        List<AssignmentQuestions> aq= assignmentQuestionsRequest.stream().map(aqr-> assignmentQuestionsUtil.mapAssignmentQestionRequest_ToAssignmentQuestions(aqr)).collect(Collectors.toList());
        assignmentQuestionRepository.saveAll(aq);
        assignmentQuestionsUtil.assignmentQuestionsGlobalList.addAll(aq);
        return aq.stream().map(aqr->assignmentQuestionsUtil.mapAssignmentQuestions_ToAssignmentQuestionResponse(aqr)).collect(Collectors.toList());

    }

    @Override
    public AssignmentQuestionsResponse submitAssignmentQuestion(AssignmentQuestionsRequest assignmentQuestionsRequest) {
        AssignmentQuestions aq= assignmentQuestionsUtil.mapAssignmentQestionRequest_ToAssignmentQuestions(assignmentQuestionsRequest);
        assignmentQuestionRepository.save(aq);
        assignmentQuestionsUtil.assignmentQuestionsGlobalList.add(aq);
        return assignmentQuestionsUtil.mapAssignmentQuestions_ToAssignmentQuestionResponse(aq);
    }
}
