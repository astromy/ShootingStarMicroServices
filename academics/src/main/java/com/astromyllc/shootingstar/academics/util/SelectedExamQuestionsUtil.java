package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.response.SelectedExamQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.SelectedExamQuestions;
import com.astromyllc.shootingstar.academics.repository.SelectedExamsQuestionsRepository;
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
public class SelectedExamQuestionsUtil {

    private final SelectedExamsQuestionsRepository selectedExamsQuestionsRepository;
    public static List<SelectedExamQuestions> selectedExamQuestionsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetAllSelectedExamQuestions() {
        selectedExamQuestionsGlobalList = selectedExamsQuestionsRepository.findAll();
        log.info("Global SelectedExamQuestionAnswers List populated with {} records", selectedExamQuestionsGlobalList.size());
    }

    public Optional<SelectedExamQuestionsResponse> mapSelectedExamsQuestion_ToSelectedExamsQuestionResponse(SelectedExamQuestions s) {
    return Optional.of(SelectedExamQuestionsResponse.builder()
                    .id(s.getId())
                    .institutionCode(s.getInstitutionCode())
                    .subjectId(s.getSubjectId())
                    .classId(s.getClassId())
                    .questionDetail(s.getQuestionDetail())
                    .term(s.getTerm())
                    .selectedExamQuestionAnswersResponses(s.getSelectedExamQuestionAnswers().stream().map(SelectedExamQuestionAnswersUtil::mapSelectedExamsQuestionAns_ToExamsQuestionAnsResponse).toList())
            .build());
    }
}
