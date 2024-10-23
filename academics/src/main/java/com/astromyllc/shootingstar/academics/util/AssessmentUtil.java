package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.alien.*;
import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.AssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssessmentResponse;
import com.astromyllc.shootingstar.academics.model.Assessment;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentUtil {
    private final AssessmentRepository assessmentRepository;

    private final WebClient.Builder webClientBuilder;
    private final ContinuousAssessmentUtil cau;
    private final ExamsAssessmentUtil eau;
    public static List<Assessment> assessmentsGlobalList;
    private static List<Students> students;
    public static List<InstitutionRequest> institutionGlobalRequest = null;
    public static List<Students> studentsGlobalRequest = null;
    private InstitutionRequest singleInstitutionGlobalRequest = null;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${gateway.host}")
    private String host;

    @Bean
    private void fetAllAssessment() {
        assessmentsGlobalList = assessmentRepository.findAll();
        log.info("Global Assessment List populated with {} records", assessmentsGlobalList.size());
    }

    @Bean
    private void fetchSetupdata() {
        institutionGlobalRequest=
                webClientBuilder.build().post()
                         .uri("http://"+host+":8083/api/setup/getAllinstitution")
                         .contentType(MediaType.APPLICATION_JSON)
                         .accept(MediaType.APPLICATION_JSON)
                         .retrieve()
                         .bodyToMono(new ParameterizedTypeReference<List<InstitutionRequest>>() {}).block();
        log.info("Global Setup List populated with {} records", institutionGlobalRequest.size());
        //return result;
    }

     //@Bean
    private void fetchStudents() {
        studentsGlobalRequest = webClientBuilder.build().post()
                .uri("http://"+host+":8083/api/administration-pta/getAllStudents")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Students>>() {}).block();
         log.info("Global Students List populated with {} records", studentsGlobalRequest.size());
    }

    private Double computeExamsScore(String studId, String academicYear, String term, Long subject) {
        Double examsScore = ExamsAssessmentUtil.examsAssessmentGlobalList.stream()
                .filter(ex -> false).mapToDouble(ExamsAssessment::getScore).sum();
        Double totalExamsScore = ExamsAssessmentUtil.examsAssessmentGlobalList.stream()
                .filter(ex -> false).mapToDouble(ExamsAssessment::getTotalScore).sum();
        return ((examsScore / totalExamsScore) * 100) * singleInstitutionGlobalRequest.getGradingSetting().getExamsPercentage();
    }

    private Double computeClassScore(String studId, String academicYear, String term, Long subject) {
        return ((sumClassScore(studId, academicYear, term, subject) / totalSumClassScore(studId, academicYear, term, subject)) * 100) * singleInstitutionGlobalRequest.getGradingSetting().getClassPercentage();
    }

    private Integer computePosition() {
        ArrayList<Students> sortedStudents = new ArrayList<>();
        return null;
    }

    private GradingRequest computeGrade(Double as) {
        return singleInstitutionGlobalRequest.getGradingSetting().getGradingList()
                .stream().filter(gr -> gr.getLowerLimit() >= as)
                .toList().stream().filter(gr1 -> gr1.getLowerLimit() <= as).findFirst().get();
    }

    private Double sumClassScore(String studId, String academicYear, String term, Long subject) {
        return ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream()
                .filter(cx -> cx.getStudentId().equalsIgnoreCase(studId)
                        && cx.getAcademicYear().equalsIgnoreCase(academicYear)
                        && cx.getTerm().equalsIgnoreCase(term)
                        && Objects.equals(cx.getSubject(), subject)).mapToDouble(ContinuousAssessment::getScore).sum();
    }

    private Double totalSumClassScore(String studId, String academicYear, String term, Long subject) {
        return ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream()
                .filter(cx -> cx.getStudentId().equalsIgnoreCase(studId)
                        && cx.getAcademicYear().equalsIgnoreCase(academicYear)
                        && cx.getTerm().equalsIgnoreCase(term)
                        && Objects.equals(cx.getSubject(), subject)).mapToDouble(ContinuousAssessment::getTotalScore).sum();
    }

    public List<Assessment> insertAssessment(AcademicReportRequest terminalReportRequest) {
        if (institutionGlobalRequest == null) {
            // fetchSetupdata();
            fetchStudents();
        }
        if (singleInstitutionGlobalRequest == null) {
            singleInstitutionGlobalRequest = institutionGlobalRequest.stream().filter(i -> i.getBececode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).findFirst().get();
        }
        List<Students> localStudents = studentsGlobalRequest.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).collect(Collectors.toList());
        List<SubjectRequest> localSubject = singleInstitutionGlobalRequest.getSubjectList().stream().filter(s -> s.getClassGroup().equalsIgnoreCase(terminalReportRequest.getClassGroup())).collect(Collectors.toList());
        List<ClassesRequest> localClasses = singleInstitutionGlobalRequest.getClassList();
        List<Assessment> builtAssessment = null;
        //localClasses.stream() .map(c->localSubject.stream().map(sub->localStudents.stream().map(stud->buildAssessment(terminalReportRequest,stud,sub)))).collect(Collectors.)
        for (ClassesRequest c : localClasses) {
            for (SubjectRequest s : localSubject) {
                for (Students stud : localStudents) {
                    builtAssessment.add(buildAssessment(terminalReportRequest, stud, s));
                }
            }
        }
        assert builtAssessment != null;
        List<Assessment> assessmentsWithoutPositions= builtAssessment.stream().sorted(createPersonLambdaComparator()).collect(Collectors.toList());
        for(int i=0;i<assessmentsWithoutPositions.size();i++){
            assessmentsWithoutPositions.get(i).setPosition(i+1);
        }
        return assessmentsWithoutPositions;
    }

    public static Comparator<Assessment> createPersonLambdaComparator() {
        return Comparator.comparing(Assessment::getTotalScore);
    }

    public Assessment buildAssessment(AcademicReportRequest terminalReportRequest, Students stud, SubjectRequest sub) {
        Double classPercentage = computeClassScore(stud.getStudentId(), terminalReportRequest.getAcademicYear(), terminalReportRequest.getTerm(), sub.getId());
        Double examsPercentage = computeExamsScore(stud.getStudentId(), terminalReportRequest.getAcademicYear(), terminalReportRequest.getTerm(), sub.getId());
        return Assessment.builder()
                .academicYear(terminalReportRequest.getAcademicYear())
                .dateTime(LocalDateTime.now())
                .studentClass(terminalReportRequest.getTargetClass())
                .term(terminalReportRequest.getTerm())
                .studentId(stud.getStudentId())
                .subject(sub.getName())
                .classScore(classPercentage)
                .examsScore(examsPercentage)
                .totalScore(classPercentage + examsPercentage)
                .institutionCode(terminalReportRequest.getInstitutionCode())
                .grade(computeGrade(classPercentage + examsPercentage).getGrade())
                .build();
    }

    public AssessmentRequest buildAssessmentRequest(AcademicReportRequest terminalReportRequest) {
        return AssessmentRequest.builder()
                .academicYear(terminalReportRequest.getAcademicYear())
                .institutionCode(terminalReportRequest.getInstitutionCode())
                .term(terminalReportRequest.getTerm())
                .studentClass(terminalReportRequest.getTargetClass())
                .build();

    }

    public AssessmentResponse mapAssessment_ToAssessmentResponse(Assessment a) {
        return AssessmentResponse.builder()
                .academicYear(a.getAcademicYear())
                .dateTime(LocalDateTime.now())
                .studentClass(a.getStudentClass())
                .term(a.getTerm())
                .studentId(a.getStudentId())
                .subject(a.getSubject())
                .classScore(a.getClassScore().toString())
                .examsScore(a.getExamsScore().toString())
                .totalScore(a.getTotalScore().toString())
                .institutionCode(a.getInstitutionCode())
                .grade(a.getGrade())
                .position(a.getPosition().toString())
                .build();
    }
}
