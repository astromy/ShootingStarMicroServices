package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentQuestionsResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.AssignmentQuestionsServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/api/academics/")
@Slf4j
@RequiredArgsConstructor
public class AssignmentQuestionController {
    private final AssignmentQuestionsServiceInterface assignmentQuestionsServiceInterface;

    @PostMapping("submitAssignmentQuestion")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AssignmentQuestionsResponse> submitAssignmentQuestion(@RequestBody AssignmentQuestionsRequest assignmentQuestionsRequest){
      return ResponseEntity.ok(assignmentQuestionsServiceInterface.submitAssignmentQuestion(assignmentQuestionsRequest));
    }

    @PostMapping("submitAssignmentQuestions")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AssignmentQuestionsResponse>> submitAssignmentQuestions(@RequestBody List<AssignmentQuestionsRequest> assignmentQuestionsRequest){
        return ResponseEntity.ok(assignmentQuestionsServiceInterface.submitAssignmentQuestions(assignmentQuestionsRequest));
    }
}
