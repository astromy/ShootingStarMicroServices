package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.alien.StudentScores;
import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.repository.ContinuousAssessmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.astromyllc.shootingstar.academics.util.AssessmentUtil.institutionGlobalRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContinuousAssessmentUtil {

    private final ContinuousAssessmentRepository assessmentRepository;
    public static List<ContinuousAssessment> continuousAssessmentGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
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
                .dateTime(LocalDateTime.parse((LocalDateTime.now()).format(formatter), formatter))
                .institutionCode(car.getInstitutionCode())
                .build();
    }



    public static Map<String, Map<Long, Map<String, StudentScores>>> CalculateScores(List<ContinuousAssessment> assessments) {
        return assessments.stream()
                .collect(Collectors.groupingBy(
                        ca -> ca.getStudentClass() != null ? ca.getStudentClass() : "NULL_CLASS",
                        Collectors.groupingBy(
                                ca -> ca.getSubject() != null ? ca.getSubject() : -1L,
                                Collectors.toMap(
                                        ca -> ca.getStudentId() != null ? ca.getStudentId() : "NULL_STUDENT_ID",
                                        req -> StudentScores.builder()
                                                .studentId(req.getStudentId())
                                                .totalScoreObtained(req.getScore())
                                                .totalScorePossible(req.getTotalScore())
                                                .hasNullData(req.getStudentId() == null ||
                                                        req.getScore() == null ||
                                                        req.getTotalScore() == null)
                                                .build(),
                                        StudentScores::mergePreservingNulls
                                )
                        )
                ));
    }
}
