package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentQuestionsResponse;

import java.util.List;

public interface AssignmentQuestionsServiceInterface {

    List<AssignmentQuestionsResponse> submitAssignmentQuestions(List<AssignmentQuestionsRequest> assignmentQuestionsRequest);

    AssignmentQuestionsResponse submitAssignmentQuestion(AssignmentQuestionsRequest assignmentQuestionsRequest);
}
