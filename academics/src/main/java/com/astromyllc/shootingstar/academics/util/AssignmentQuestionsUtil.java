package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.model.AssignmentQuestions;
import com.astromyllc.shootingstar.academics.repository.AssignmentQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentQuestionsUtil {

    private final AssignmentQuestionRepository assignmentQuestionRepository;
    public static List<AssignmentQuestions> assignmentQuestionsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllAssignmentQuestions() {
        assignmentQuestionsGlobalList = assignmentQuestionRepository.findAll();
        log.info("Global AssignmentQuestions List populated with {} records", assignmentQuestionsGlobalList.size());
    }

}
