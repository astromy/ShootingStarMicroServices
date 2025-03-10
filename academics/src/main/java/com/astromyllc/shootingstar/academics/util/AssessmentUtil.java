package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.alien.*;
import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.AssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.academics.dto.response.*;
import com.astromyllc.shootingstar.academics.model.Assessment;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static List<LookupResponse> lookUpGlobalResponse=null;
    public static List<InstitutionRequest> institutionGlobalRequest = null;
    public static List<Students> studentsGlobalRequest = null;
    private InstitutionRequest singleInstitutionGlobalRequest = null;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${gateway.host}")
    private String host;
    private String cg;
    private List<Double> classAvrage=new ArrayList<>();

    @Bean
    private void fetAllAssessment() {
        assessmentsGlobalList = assessmentRepository.findAll();
        log.info("Global Assessment List populated with {} records", assessmentsGlobalList.size());
    }


    public void fetchSetupdata(String institutionCode) {
        if(singleInstitutionGlobalRequest!=null){
            SingleStringRequest request = SingleStringRequest.builder()
                    .val(institutionCode)
                    .build();
            singleInstitutionGlobalRequest =
                    WebClient.builder().build().post()
                            .uri("http://" + host + "/api/setup/getInstitutionByCode")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .bodyValue(request)
                            .retrieve()
                            .bodyToMono(InstitutionRequest.class)
                            .block();
 /*           institutionGlobalRequest=
                    webClientBuilder.build().post()
                             .uri("http://"+host+"/api/setup/getInstitutionByCode")
                             .contentType(MediaType.APPLICATION_JSON)
                             .accept(MediaType.APPLICATION_JSON)
                             .retrieve()
                             .bodyToMono(new ParameterizedTypeReference<List<InstitutionRequest>>() {}).block();*/
            log.info("Global Setup List populated with {} records", institutionGlobalRequest.size());
            //return result;
        }
    }

     @Bean
    private void fetchStudents() {
        studentsGlobalRequest = webClientBuilder.build().post()
                .uri("http://"+host+"/api/administration-pta/getAllStudents")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Students>>() {}).block();
         log.info("Global Students List populated with {} records", studentsGlobalRequest.size());
    }
    @Bean
    private void fetchClassGroups() {
        lookUpGlobalResponse = webClientBuilder.build().post()
                .uri("http://"+host+"/api/setup/getAllLookUp")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LookupResponse>>() {}).block();
        log.info("Global Lookup List populated with {} records", lookUpGlobalResponse.size());
    }

    private Double computeExamsScore(Double total, Double actual) {
        return (Math.round(((actual / total) * singleInstitutionGlobalRequest.getGradingSetting().getExamsPercentage())* 100.0) / 100.0);
    }

    private Double computeClassScore(Double total, Double actual) {
        return (Math.round(((actual / total) * singleInstitutionGlobalRequest.getGradingSetting().getClassPercentage())* 100.0) / 100.0);
    }

    private GradingRequest computeGrade(Double studentScore) {
        return singleInstitutionGlobalRequest.getGradingSetting().getGradingList()
                .stream()
                .filter(gr -> gr.getLowerLimit() <= studentScore) // Keep grades that apply
                .max(Comparator.comparing(gr -> gr.getLowerLimit())) // Get the one with the highest lower limit
                .orElse(null); // Handle cases where no grade is found
    }

    public List<Assessment> insertPositions(List<Assessment> assessmentsWithoutPositions) {
        // Group assessments by subject
        Map<String, List<Assessment>> groupedBySubject = assessmentsWithoutPositions.stream()
                .collect(Collectors.groupingBy(Assessment::getSubject));

        // Process each subject separately
        groupedBySubject.forEach((subject, assessments) -> {
            AtomicInteger positionCounter = new AtomicInteger(1);

            // Sort each group by total score in descending order
            assessments.sort(Comparator.comparingDouble(Assessment::getTotalScore).reversed());

            // Assign positions within the group
            assessments.forEach(a -> a.setPosition(positionCounter.getAndIncrement()));
        });

        // Flatten the grouped values back into a single list
        return groupedBySubject.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static Comparator<Assessment> createPersonLambdaComparator() {
        return Comparator.comparing(Assessment::getTotalScore);
    }

    public Assessment buildAssessment(Assessment assessments) {
        GradingRequest g=computeGrade(assessments.getClassScore()+ assessments.getExamsScore());
        assessments.setDateTime(LocalDateTime.now());
        assessments.setTotalScore(BigDecimal.valueOf(assessments.getClassScore()+ assessments.getExamsScore())
                .setScale(2, RoundingMode.HALF_UP) // Round to 2 decimal places
                .doubleValue()
        );
        assessments.setGrade(g.getGrade());
        assessments.setGradeRemarks(g.getComment());
        return assessments;
    }

    public TerminalReportResponse getTerminalReportResponse(List<AssessmentResponse> assessmentResponses, List<Students> studentsGlobalRequest,AcademicReportRequest terminalReportRequest) {
        if (singleInstitutionGlobalRequest==null) {
            singleInstitutionGlobalRequest = institutionGlobalRequest.stream().filter(i -> i.getBececode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).findFirst().get();
        }
      return  buildTerminalReportResponse(assessmentResponses,studentsGlobalRequest);
    }

    public TerminalReportResponse getTerminalReportResponse(List<AssessmentResponse> assessmentResponses, SingleStringRequest studId) {
        Students stud=studentsGlobalRequest.stream().filter(s->s.getStudentId().equalsIgnoreCase(studId.getVal())).findFirst().get();
        if (singleInstitutionGlobalRequest==null) {
            fetchSetupdata(stud.getInstitutionCode());
        }
        return  buildTerminalReportResponse(assessmentResponses,stud);
    }

    public TerminalReportResponse buildTerminalReportResponse(List<AssessmentResponse> assessmentResponses, List<Students> studentsGlobalRequest) {

        // Step 1: Group assessmentResponses by studentId
        Map<String, List<AssessmentResponse>> groupedAssessments = (assessmentResponses != null)
                ? assessmentResponses.stream().collect(Collectors.groupingBy(AssessmentResponse::getStudentId))
                : new HashMap<>();

        // Step 2: Map students to StudentReportResponse with their assessments
        List<StudentReportResponse> studentReports = (studentsGlobalRequest != null)
                ? studentsGlobalRequest.stream()
                .filter(student -> {
                    String studentId = student.getStudentId();
                    return studentId != null && groupedAssessments.containsKey(studentId) && !groupedAssessments.get(studentId).isEmpty();
                })
                .map(student -> mapAssessmentResponse_ToStudentReportResponse(student, groupedAssessments.get(student.getStudentId())))
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Step 3: Build TerminalReportResponse
        return TerminalReportResponse.builder()
                .institutionDetail(buildReportInstitutionResponse()) // Assuming this method is implemented
                .studentReportResponseList(studentReports)
                .build();
    }

    /**
     * FOr Transcript Instance
     * */
    public TerminalReportResponse buildTerminalReportResponse(List<AssessmentResponse> assessmentResponses, Students student) {


        // Step 1: Group assessmentResponses by studentId
        Map<String, List<AssessmentResponse>> groupedAssessments =
                (assessmentResponses != null)
                        ? assessmentResponses.stream().collect(Collectors.groupingBy(AssessmentResponse::getStudentId))
                        : new HashMap<>();

        List<StudentReportResponse> studentReports = new ArrayList<>();

        if (student != null) {
            String studentId = student.getStudentId();
            if (studentId != null && groupedAssessments.containsKey(studentId) && !groupedAssessments.getOrDefault(studentId, Collections.emptyList()).isEmpty()) {
                studentReports.add(mapAssessmentResponse_ToStudentReportResponse(student, groupedAssessments.get(studentId)));
            }
        }

        // Step 3: Build TerminalReportResponse
        return TerminalReportResponse.builder()
                .institutionDetail(buildReportInstitutionResponse()) // Assuming this method is implemented
                .studentReportResponseList(studentReports)
                .build();
    }


    public ReportInstitutionResponse buildReportInstitutionResponse() {
        return ReportInstitutionResponse.builder()
                .id(singleInstitutionGlobalRequest.getId())
                .city(singleInstitutionGlobalRequest.getCity())
                .email(singleInstitutionGlobalRequest.getEmail())
                .name(singleInstitutionGlobalRequest.getName())
                .contact2(singleInstitutionGlobalRequest.getContact2())
                .country(singleInstitutionGlobalRequest.getCountry())
                .postalAddress(singleInstitutionGlobalRequest.getPostalAddress())
                .region(singleInstitutionGlobalRequest.getRegion())
                .slogan(singleInstitutionGlobalRequest.getSlogan())
                .website(singleInstitutionGlobalRequest.getWebsite())
                .bececode(singleInstitutionGlobalRequest.getBececode())
                .contact1(singleInstitutionGlobalRequest.getContact1())
                .crest(singleInstitutionGlobalRequest.getCrest())
                .classGroup(
                        Optional.ofNullable(cg)
                                .map(cgValue -> lookUpGlobalResponse.stream()
                                        .filter(lr -> lr.getId().equals(Long.valueOf(cgValue)))
                                        .map(LookupResponse::getName)
                                        .findFirst()
                                        .orElse(null)
                                )
                                .orElse(null)
                )
                .classAverage(String.valueOf( BigDecimal.valueOf(
                        (classAvrage.stream().mapToDouble(Double::doubleValue).sum())/classAvrage.size())
                        .setScale(2, RoundingMode.HALF_UP) // Round to 2 decimal places
                        .doubleValue()
                ))
                .build();
    }

    public AssessmentResponse mapAssessment_ToAssessmentResponse(Assessment a,String classGroup) {
        cg=classGroup;
        return AssessmentResponse.builder()
                .academicYear(a.getAcademicYear())
                .dateTime(a.getDateTime())
                .studentClass(a.getStudentClass())
                .term(a.getTerm())
                .studentId(a.getStudentId())
                .subject(a.getSubject())
                .classScore(a.getClassScore().toString())
                .examsScore(a.getExamsScore().toString())
                .totalScore(a.getTotalScore().toString())
                .institutionCode(a.getInstitutionCode())
                .grade(a.getGrade())
                .gradeRemarks(a.getGradeRemarks())
                .position(a.getPosition().toString())
                .build();
    }

    public AssessmentResponse mapAssessment_ToAssessmentResponse(Assessment a) {
        return AssessmentResponse.builder()
                .academicYear(a.getAcademicYear())
                .dateTime(a.getDateTime())
                .studentClass(a.getStudentClass())
                .term(a.getTerm())
                .studentId(a.getStudentId())
                .subject(a.getSubject())
                .classScore(a.getClassScore().toString())
                .examsScore(a.getExamsScore().toString())
                .totalScore(a.getTotalScore().toString())
                .institutionCode(a.getInstitutionCode())
                .grade(a.getGrade())
                .gradeRemarks(a.getGradeRemarks())
                .position(a.getPosition().toString())
                .build();
    }


    public StudentReportResponse mapAssessmentResponse_ToStudentReportResponse(Students student, List<AssessmentResponse> assessments) {
       double as=calculateTotalAverage(assessments);
        GradingRequest g=computeGrade(as);
        return StudentReportResponse.builder()
                .id(UUID.randomUUID().toString()) // Assuming an ID is required
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .otherName(student.getOtherName())
                .lastName(student.getLastName())
                .gender(student.getGender())
                .averageScore(String.valueOf(BigDecimal.valueOf(as).setScale(2, RoundingMode.HALF_UP).doubleValue()))
                .averageGrade(g.getGrade())
                .averageRemark(g.getComment())
                .studentAssessment(assessments)
                .build();
    }

    public double calculateTotalAverage(List<AssessmentResponse> assessments) {
        if (assessments == null || assessments.isEmpty()) {
            return 0.0; // Return 0 if there are no assessments
        }

        double totalSum = assessments.stream()
                .mapToDouble(a -> parseDouble(a.getTotalScore()))
                .sum();

        int subjectCount = assessments.size(); // Each assessment represents a subject
        double ta=((totalSum / (subjectCount * 100))*100); // Assuming each subject is out of 100
        classAvrage.add(ta);
        return ta;
    }

    private double parseDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0; // Handle invalid numbers gracefully
        }
    }

    public List<Assessment> passExamsAssessment(Map.Entry<String, Map<Long, Map<String, StudentScores>>> sr,AcademicReportRequest terminalReportRequest) {
        String studentClass = sr.getKey(); // Example: "ClassA"
        singleInstitutionGlobalRequest=institutionGlobalRequest.stream().filter(i->i.getBececode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).findFirst().get();
        return sr.getValue().entrySet().stream() // Iterate over subjects
                .flatMap(subjectEntry -> {
                    Long subjectId = subjectEntry.getKey(); // Example: 101

                    return subjectEntry.getValue().entrySet().stream() // Iterate over students
                            .map(studentEntry -> {
                                String studentId = studentEntry.getKey(); // Example: "S001"
                                StudentScores scores = studentEntry.getValue(); // Example: (Sum Score: 80.0, Sum Total Score: 200.0)

                                // Build the assessment object
                                return Assessment.builder()
                                        .academicYear(terminalReportRequest.getAcademicYear())
                                        .dateTime(LocalDateTime.now())
                                        .studentClass(studentClass) // "ClassA"
                                        .term(terminalReportRequest.getTerm())
                                        .studentId(studentId) // "S001"
                                        .subject(singleInstitutionGlobalRequest.getSubjectList().stream().filter(sb-> Objects.equals(sb.getId(), subjectId)).findFirst().get().getName()) // "Mathematics" (Example)
                                        .examsScore( Math.round(computeExamsScore(scores.getTotalScorePossible(),scores.getTotalScoreObtained())* 100.0) / 100.0 )   // 200.0
                                        //.totalScore(Math.round(scores.getTotalScorePossible()* 100.0) / 100.0) // 280.0
                                        .institutionCode(terminalReportRequest.getInstitutionCode())
                                        //.grade(computeGrade(scores.getTotalScoreObtained()).getGrade())
                                        .build();
                            });
                })
                .collect(Collectors.toList()); // Collect all assessments
    }
    public List<Assessment> passContinuousAssessment(Map.Entry<String, Map<Long, Map<String, StudentScores>>> sr, AcademicReportRequest terminalReportRequest, StudentScores studentScores) {
        String studentClass = sr.getKey(); // Example: "ClassA"
        singleInstitutionGlobalRequest=institutionGlobalRequest.stream().filter(i->i.getBececode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).findFirst().get();

        return sr.getValue().entrySet().stream() // Iterate over subjects
                .flatMap(subjectEntry -> {
                    Long subjectId = subjectEntry.getKey(); // Example: 101

                    return subjectEntry.getValue().entrySet().stream() // Iterate over students
                            .map(studentEntry -> {
                                String studentId = studentEntry.getKey(); // Example: "S001"
                                StudentScores scores = studentEntry.getValue(); // Example: (Sum Score: 80.0, Sum Total Score: 200.0)

                                // Build the assessment object
                                return Assessment.builder()
                                        .academicYear(terminalReportRequest.getAcademicYear())
                                        .dateTime(LocalDateTime.now())
                                        .studentClass(studentClass) // "ClassA"
                                        .term(terminalReportRequest.getTerm())
                                        .studentId(studentId) // "S001"
                                        .subject(singleInstitutionGlobalRequest.getSubjectList().stream().filter(sb-> Objects.equals(sb.getId(), subjectId)).findFirst().get().getName()) // "Mathematics" (Example)
                                        .classScore(Math.round(computeClassScore(studentScores.getTotalScorePossible(),scores.getTotalScoreObtained())* 100.0) / 100.0) // 200.0
                                        .institutionCode(terminalReportRequest.getInstitutionCode())
                                        //.grade(computeGrade(scores.getTotalScoreObtained() + scores.getTotalScorePossible()).getGrade())
                                        .build();
                            });
                })
                .collect(Collectors.toList()); // Collect all assessments
    }


}
