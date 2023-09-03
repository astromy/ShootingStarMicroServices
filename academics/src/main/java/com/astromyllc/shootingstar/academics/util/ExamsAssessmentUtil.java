package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.ExamsAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.astromyllc.shootingstar.academics.util.AssessmentUtil.institutionGlobalRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamsAssessmentUtil {

    private final ExamsAssessmentRepository assessmentRepository;
    public static List<ExamsAssessment> examsAssessmentGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllExamsAssessment() {
        examsAssessmentGlobalList = assessmentRepository.findAll();
        log.info("Global ExamsAssessment List populated with {} records", examsAssessmentGlobalList.size());
    }

    public ExamsAssessment mapExamsAssessmentRequest_ToExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest) {
        return ExamsAssessment.builder()
                .dateTime(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter))
                .term(examsAssessmentRequest.getTerm())
                .academicYear(examsAssessmentRequest.getAcademicYear())
                .score(examsAssessmentRequest.getScore())
                .studentClass(examsAssessmentRequest.getStudentClass())
                .subject(examsAssessmentRequest.getSubject())
                .institutionCode(examsAssessmentRequest.getInstitutionCode())
                .studentId(examsAssessmentRequest.getStudentId())
                .build();
    }

    public ExamsAssessmentResponse mapExamsAssessment_ToExamsAssessmentResponse(ExamsAssessment car) {
        String sub=institutionGlobalRequest.stream().filter(i -> i.getBececode().equals(car.getInstitutionCode())).findFirst().get().getSubjectList().stream().filter(s -> s.getId().equals(car.getSubject())).findFirst().get().getName();
        return ExamsAssessmentResponse
                .builder()
                .dateTime(car.getDateTime())
                .term(car.getTerm())
                .academicYear(car.getAcademicYear())
                .score(car.getScore())
                .studentClass(car.getStudentClass())
                .subject(sub)
                .institutionCode(car.getInstitutionCode())
                .studentId(car.getStudentId())
                .build();
    }
}
