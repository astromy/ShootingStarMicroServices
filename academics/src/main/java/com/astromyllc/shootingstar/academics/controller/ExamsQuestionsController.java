package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsQuestionsServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/academics/")
public class ExamsQuestionsController {

    private final ExamsQuestionsServiceInterface examsQuestionsServiceInterface;

    @PostMapping("submitExamsQuestion")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ExamsQuestionsResponse> submitExamsQuestion(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
      return ResponseEntity.ok( examsQuestionsServiceInterface.submitQuestion(examsQuestionsRequest));
    }

    @PostMapping("submitExamsQuestions")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<ExamsQuestionsResponse>> submitExamsQuestions(@RequestBody List<ExamsQuestionsRequest> examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.submitQuestions(examsQuestionsRequest));
    }

}
