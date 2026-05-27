package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.model.SelectedAssignmentQuestions;
import com.astromyllc.shootingstar.academics.repository.SelectedAssignmentQuestionsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelectedAssignmentQuestionsUtil {

    private final SelectedAssignmentQuestionsRepository selectedAssignmentQuestionsRepository;
    public static List<SelectedAssignmentQuestions> selectedAssignmentQuestionsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetAllSelectedAssignmentQuestions() {
        selectedAssignmentQuestionsGlobalList = selectedAssignmentQuestionsRepository.findAll();
        log.info("Global SelectedAssignmentQuestions List populated with {} records", selectedAssignmentQuestionsGlobalList.size());
    }

}
