package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.ClassListRequest;
import com.astromyllc.shootingstar.academics.dto.alien.StudentScores;
import com.astromyllc.shootingstar.academics.dto.alien.Students;
import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.ExamsAssessmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.astromyllc.shootingstar.academics.util.AssessmentUtil.institutionGlobalRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamsAssessmentUtil {

    private final ExamsAssessmentRepository assessmentRepository;
    public static List<ExamsAssessment> examsAssessmentGlobalList;

    private final WebClient.Builder webClientBuilder;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${gateway.host}")
    private String host;

    @PostConstruct
    private void fetAllExamsAssessment() {
        examsAssessmentGlobalList = assessmentRepository.findAll();
        log.info("Global ExamsAssessment List populated with {} records", examsAssessmentGlobalList.size());
    }


    public List<ClassListResponse> fetchStudentsByClass(String studentClass, String institutionCode) {
        ClassListRequest request = ClassListRequest.builder()
                .institutionCode(institutionCode)
                .studentClass(studentClass)
                .build();
         List<ClassListResponse>   response= webClientBuilder.build().post()
                .uri("http://"+host+"/api/administration-pta/getAssessmentList")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ClassListResponse>>() {}).block();
         return response;
    }

    public ExamsAssessment mapExamsAssessmentRequest_ToExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest) {
        return ExamsAssessment.builder()
                .dateTime(LocalDateTime.parse((LocalDateTime.now()).format(formatter), formatter))
                .term(examsAssessmentRequest.getTerm())
                .academicYear(examsAssessmentRequest.getAcademicYear())
                .score(examsAssessmentRequest.getScore())
                .studentClass(examsAssessmentRequest.getStudentClass())
                .subject(examsAssessmentRequest.getSubject())
                .institutionCode(examsAssessmentRequest.getInstitutionCode())
                .studentId(examsAssessmentRequest.getStudentId())
                .totalScore(examsAssessmentRequest.getTotalScore())
                .build();
    }

    public Optional<ExamsAssessmentResponse> mapExamsAssessment_ToExamsAssessmentResponse(ExamsAssessment car) {
        String sub=institutionGlobalRequest.stream().filter(i -> i.getBececode().equalsIgnoreCase(car.getInstitutionCode())).findFirst().get().getSubjectList().stream().filter(s -> s.getId().equals(car.getSubject())).findFirst().get().getName();
        return Optional.of(ExamsAssessmentResponse
                .builder()
                .dateTime(car.getDateTime())
                .term(car.getTerm())
                .academicYear(car.getAcademicYear())
                .score(car.getScore())
                .studentClass(car.getStudentClass())
                .subject(sub)
                .institutionCode(car.getInstitutionCode())
                .studentId(car.getStudentId())
                .build());
    }

    public static Map<String, Map<Long, Map<String, StudentScores>>> CalculateExamsScores(List<ExamsAssessment> assessments) {
        return assessments.stream()
                .collect(Collectors.groupingBy(
                        ExamsAssessment::getStudentClass,
                        Collectors.groupingBy(
                                ExamsAssessment::getSubject,
                                Collectors.toMap(
                                        ExamsAssessment::getStudentId,
                                        req -> StudentScores.builder()
                                                .studentId(req.getStudentId())
                                                .totalScoreObtained(req.getScore())
                                                .totalScorePossible(req.getTotalScore())
                                                .hasNullData(req.getStudentId() == null ||
                                                        req.getScore() == null ||
                                                        req.getTotalScore() == null)
                                                .build(),
                                        StudentScores::merge
                                )
                        )
                ));
    }
}
