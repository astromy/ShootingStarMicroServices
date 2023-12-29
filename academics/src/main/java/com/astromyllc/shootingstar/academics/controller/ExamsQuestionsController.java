package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionAnswersResponse;
import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;
import com.astromyllc.shootingstar.academics.service.SelectedExamQuestionsService;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAnswersServiceInterface;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsQuestionsServiceInterface;
import com.astromyllc.shootingstar.academics.serviceInterface.SelectedExamQuestionAnswersServiceInterface;
import com.astromyllc.shootingstar.academics.serviceInterface.SelectedExamQuestionsServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/academics/")
public class ExamsQuestionsController {

    private final ExamsQuestionsServiceInterface examsQuestionsServiceInterface;
    private final ExamsAnswersServiceInterface examsAnswersServiceInterface;
    private final SelectedExamQuestionsServiceInterface selectedExamQuestionsServiceInterface;
    private final SelectedExamQuestionAnswersServiceInterface selectedExamQuestionAnswersServiceInterface;

//================================================= Exams Questions ===================================================
    @PostMapping("submitExamsQuestion")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<ExamsQuestionsResponse>> submitExamsQuestion(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
      return ResponseEntity.ok( examsQuestionsServiceInterface.submitQuestion(examsQuestionsRequest));
    }

    @PostMapping("submitExamsQuestions")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> submitExamsQuestions(@RequestBody List<ExamsQuestionsRequest> examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.submitQuestions(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByClassAndTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByClassAndTerm(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByClassAndTerm(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByClass")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByClass(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByClass(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByInstAndClassAndTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByInstAndClassAndTerm(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByInstAndClassAndTerm(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByInstAndClass")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByInstAndClass(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByInstAndClass(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByInstAndClassAndSubj")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByInstAndClassAndSubj(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByInstAndClassAndSubj(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByInstAndClassAndSubjAndTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByInstAndClassAndSubjAndTerm(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByInstAndClassAndSubjAndTerm(examsQuestionsRequest));
    }

    @PostMapping("fetchQuestionsByClassAndSuj")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<ExamsQuestionsResponse>>> fetchQuestionsByClassAndSuj(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(examsQuestionsServiceInterface.fetchQuestionsByClassAndSuj(examsQuestionsRequest));
    }

//================================================ Selected Exams Questions ===========================================


    @PostMapping("fetchSelectedQuestionsByClassAndTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByClassAndTerm(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByClassAndTerm(examsQuestionsRequest));
    }

    @PostMapping("fetchSelectedQuestionsByClass")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByClass(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByClass(examsQuestionsRequest));
    }

    @PostMapping("fetchSelectedQuestionsByInstAndClassAndTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByInstAndClassAndTerm(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByInstAndClassAndTerm(examsQuestionsRequest));
    }

    @PostMapping("fetchSelectedQuestionsByInstAndClass")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByInstAndClass(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByInstAndClass(examsQuestionsRequest));
    }

    @PostMapping("fetchSelectedQuestionsByInstAndClassAndSubj")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByInstAndClassAndSubj(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByInstAndClassAndSubj(examsQuestionsRequest));
    }

    @PostMapping("fetchSelectedQuestionsByInstAndClassAndSubjAndTerm")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByInstAndClassAndSubjAndTerm(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByInstAndClassAndSubjAndTerm(examsQuestionsRequest));
    }

    @PostMapping("fetchSelectedQuestionsByClassAndSuj")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<Optional<SelectedExamQuestionsResponse>>> fetchSelectedQuestionsByClassAndSuj(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionsServiceInterface.fetchSelectedQuestionsByClassAndSuj(examsQuestionsRequest));
    }



//================================================= Exams Answers =====================================================

@PostMapping("fetchAnsToQuestion")
@ResponseStatus(HttpStatus.CREATED)
public ResponseEntity<Optional<SelectedExamQuestionAnswersResponse>> fetchAnsToQuestion(@RequestBody ExamsQuestionsRequest examsQuestionsRequest){
    return ResponseEntity.ok( selectedExamQuestionAnswersServiceInterface.fetchAnsToQuestion(examsQuestionsRequest));
}

    @PostMapping("fetchAnsToQuestionList")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<Optional<SelectedExamQuestionAnswersResponse>>> fetchAnsToQuestionList(@RequestBody List<ExamsQuestionsRequest> examsQuestionsRequest){
        return ResponseEntity.ok(selectedExamQuestionAnswersServiceInterface.fetchAnsToQuestionList(examsQuestionsRequest));
    }

}
