package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionAnswersResponse;
import com.astromyllc.shootingstar.academics.model.SelectedExamQuestionAnswers;
import com.astromyllc.shootingstar.academics.repository.SelectedExamsQuestionAnswersRepository;
import jakarta.annotation.PostConstruct;
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
public class SelectedExamQuestionAnswersUtil {

    private final SelectedExamsQuestionAnswersRepository selectedExamsQuestionAnswersRepository;
    public static List<SelectedExamQuestionAnswers> selectedExamQuestionAnswersGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetAllSelectedExamQuestionAnswers() {
        selectedExamQuestionAnswersGlobalList = selectedExamsQuestionAnswersRepository.findAll();
        log.info("Global SelectedExamQuestionAnswers List populated with {} records", selectedExamQuestionAnswersGlobalList.size());
    }

    public static Optional<SelectedExamQuestionAnswersResponse> mapSelectedExamsQuestionAns_ToExamsQuestionAnsResponse(SelectedExamQuestionAnswers sea) {
    return Optional.of(SelectedExamQuestionAnswersResponse.builder()
                    .id(sea.getId())
                    .answer(sea.getAnswer())
                    .isQuestionAnswer(sea.getIsQuestionAnswer())
            .build());
    }

}
