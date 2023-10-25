package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentQuestionsRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentQuestionsResponse;
import com.astromyllc.shootingstar.academics.model.AssignmentQuestions;
import com.astromyllc.shootingstar.academics.repository.AssignmentQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    public AssignmentQuestions mapAssignmentQestionRequest_ToAssignmentQuestions(AssignmentQuestionsRequest aqr) {
      return   AssignmentQuestions.builder()
              .questionDetail(aqr.getQuestionDetail())
              .classId(aqr.getClassId())
              .term(aqr.getTerm())
              .subjectId(aqr.getSubjectId())
              .institutionCode(aqr.getInstitutionCode())
              .assignmentAnswers(aqr.getAssignmentAnswersRequests().stream().map(aqa->AssignmentAnswersUtil.mapAnswerRequestToAssignmentAnswers(aqa)).collect(Collectors.toList()))
              .build();
    }

    public AssignmentQuestionsResponse mapAssignmentQuestions_ToAssignmentQuestionResponse(AssignmentQuestions aqr) {
        return AssignmentQuestionsResponse.builder()
                .questionDetail(aqr.getQuestionDetail())
                .classId(aqr.getClassId())
                .term(aqr.getTerm())
                .subjectId(aqr.getSubjectId())
                .institutionCode(aqr.getInstitutionCode())
                .assignmentAnswersResponses(aqr.getAssignmentAnswers().stream().map(aqa->AssignmentAnswersUtil.mapAnswerRequestToAssignmentAnswersResponse(aqa)).collect(Collectors.toList()))
                .build();
    }
}
