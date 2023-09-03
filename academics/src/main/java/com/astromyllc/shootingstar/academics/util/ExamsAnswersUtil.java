package com.astromyllc.shootingstar.academics.util;

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

}
