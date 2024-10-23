package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.repository.ContinuousAssessmentRepository;
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
public class ContinuousAssessmentUtil {

    private final ContinuousAssessmentRepository assessmentRepository;
    public static List<ContinuousAssessment> continuousAssessmentGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllContinuousAssessment() {
        continuousAssessmentGlobalList = assessmentRepository.findAll();
        log.info("Global ContinuousAssessment List populated with {} records", continuousAssessmentGlobalList.size());
    }

    public ContinuousAssessmentResponse mapContinuousAssessment_ToContinuousAssessmentResponse(ContinuousAssessment car) {
       String sub=institutionGlobalRequest.stream().filter(i -> i.getBececode().equalsIgnoreCase(car.getInstitutionCode())).findFirst().get().getSubjectList().stream().filter(s -> s.getId().equals(car.getSubject())).findFirst().get().getName();
        return ContinuousAssessmentResponse.builder()
               .id(car.getId())
               .score(car.getScore())
                .totalScore(car.getTotalScore())
               .term(car.getTerm())
               .studentId(car.getStudentId())
               .academicYear(car.getAcademicYear())
               .subject(sub)
               .studentClass(car.getStudentClass())
               .dateTime(car.getDateTime())
               .institutionCode(car.getInstitutionCode())
               .build();
    }

    public ContinuousAssessment mapContinuousAssessmentRequest_ToContinuousAssessment(ContinuousAssessmentRequest car) {
        return ContinuousAssessment.builder()
                .id(car.getId())
                .score(car.getScore())
                .totalScore(car.getTotalScore())
                .term(car.getTerm())
                .studentId(car.getStudentId())
                .academicYear(car.getAcademicYear())
                .subject(car.getSubject())
                .studentClass(car.getStudentClass())
                .dateTime(LocalDateTime.parse(car.getDateTime(),formatter))
                .institutionCode(car.getInstitutionCode())
                .build();
    }
}
