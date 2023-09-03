package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.model.SelectedAssignmentQuestionAnswers;
import com.astromyllc.shootingstar.academics.repository.SelectedAssignmentQuestionAnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelectedAssignmentQuestionAnswersUtil {

    private final SelectedAssignmentQuestionAnswersRepository selectedAssignmentQuestionAnswersRepository;
    public static List<SelectedAssignmentQuestionAnswers> selectedAssignmentQuestionAnswersGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllSelectedAssignmentQuestionAnswers() {
        selectedAssignmentQuestionAnswersGlobalList = selectedAssignmentQuestionAnswersRepository.findAll();
        log.info("Global SelectedAssignmentQuestionAnswers List populated with {} records", selectedAssignmentQuestionAnswersGlobalList.size());
    }


}
