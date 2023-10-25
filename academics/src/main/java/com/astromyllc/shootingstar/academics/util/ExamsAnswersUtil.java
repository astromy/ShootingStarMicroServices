package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAnswersRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAnswersResponse;
import com.astromyllc.shootingstar.academics.model.ExamsAnswers;
import com.astromyllc.shootingstar.academics.repository.ExamsAnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamsAnswersUtil {

    private final ExamsAnswersRepository examsAnswersRepository;
    public static List<ExamsAnswers> examsAnswersGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Bean
    private void fetAllExamsAnswers() {
        examsAnswersGlobalList = examsAnswersRepository.findAll();
        log.info("Global ExamsAnswers List populated with {} records", examsAnswersGlobalList.size());
    }

    public static ExamsAnswers mapAnswerRequestToExamsAnswers(ExamsAnswersRequest eqa) {
       return ExamsAnswers.builder()
               .answer(eqa.getAnswer())
               .isAnswer(eqa.getIsAnswer())
               .build();
    }

    public static ExamsAnswersResponse mapAnswerRequestToExamsAnswersResponse(ExamsAnswers eqa) {
        return ExamsAnswersResponse.builder()
                .answer(eqa.getAnswer())
                .isAnswer(eqa.getIsAnswer())
                .build();
    }


}
