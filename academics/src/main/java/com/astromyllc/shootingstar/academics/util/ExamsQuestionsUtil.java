package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.ExamsQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.ExamsQuestions;
import com.astromyllc.shootingstar.academics.repository.ExamsQuestionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamsQuestionsUtil {

    private final ExamsQuestionsRepository examsQuestionsRepository;
    public static List<ExamsQuestions> examsQuestionsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllExamsQuestions() {
        examsQuestionsGlobalList = examsQuestionsRepository.findAll();
        log.info("Global ExamsQuestions List populated with {} records", examsQuestionsGlobalList.size());
    }

    public ExamsQuestions mapExamsRestionRequest_ToExamsQuestions(ExamsQuestionsRequest examsQuestionsRequest) {
        return ExamsQuestions.builder()
                .questionDetail(examsQuestionsRequest.getQuestionDetail())
                .classId(examsQuestionsRequest.getClassId())
                .term(examsQuestionsRequest.getTerm())
                .subjectId(examsQuestionsRequest.getSubjectId())
                .institutionCode(examsQuestionsRequest.getInstitutionCode())
                .examsAnswers(examsQuestionsRequest.getExamsAnswersRequests().stream().map(ExamsAnswersUtil::mapAnswerRequestToExamsAnswers).toList())
                .build();
    }

    public Optional<ExamsQuestionsResponse> mapExamsQuestions_ToExamsQuestionResponse(ExamsQuestions eq) {
        return Optional.of(ExamsQuestionsResponse.builder()
                .questionDetail(eq.getQuestionDetail())
                .classId(eq.getClassId())
                .term(eq.getTerm())
                .subjectId(eq.getSubjectId())
                .institutionCode(eq.getInstitutionCode())
                .examsAnswersResponses(eq.getExamsAnswers().stream().map(ExamsAnswersUtil::mapAnswerRequestToExamsAnswersResponse).toList())
                .build());
    }
}
