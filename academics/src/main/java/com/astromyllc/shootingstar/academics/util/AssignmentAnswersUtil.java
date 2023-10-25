package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentAnswersRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentAnswersResponse;
import com.astromyllc.shootingstar.academics.model.AssignmentAnswers;
import com.astromyllc.shootingstar.academics.repository.AssignmentAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentAnswersUtil {

    private final AssignmentAnswerRepository assignmentAnswerRepository;
    public static List<AssignmentAnswers> assignmentAnswersGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static AssignmentAnswers mapAnswerRequestToAssignmentAnswers(AssignmentAnswersRequest aqa) {
        return AssignmentAnswers.builder()
                .answer(aqa.getAnswer())
                .isAnswer(aqa.getIsAnswer())
                .build();
    }

    public static AssignmentAnswersResponse mapAnswerRequestToAssignmentAnswersResponse(AssignmentAnswers aqa) {
      return   AssignmentAnswersResponse.builder()
              .answer(aqa.getAnswer())
              .isAnswer(aqa.getIsAnswer())
              .build();
    }

    @Bean
    private void fetAllAssignmentAnswers() {
        assignmentAnswersGlobalList = assignmentAnswerRepository.findAll();
        log.info("Global Diagnosis List populated with {} records", assignmentAnswersGlobalList.size());
    }
}
