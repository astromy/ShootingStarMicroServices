package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.alien.*;
import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.academics.dto.response.*;
import com.astromyllc.shootingstar.academics.model.Assessment;
import com.astromyllc.shootingstar.academics.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
    private List<LookupResponse> lookUpGlobalResponse=null;
    public static List<InstitutionRequest> institutionGlobalRequest = null;
    public List<Students> studentsGlobalRequest = null;
    public InstitutionRequest singleInstitutionGlobalRequest = null;
    public GradingSettingRequest gs=null;

    private final ExecutorService executorService;

   /* public SendSMSJSON(){
        executorService = Executors.newCachedThreadPool();
    }*/

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
            SingleStringRequest request = SingleStringRequest.builder()
                    .val(institutionCode)
                    .build();
            singleInstitutionGlobalRequest =
                    webClientBuilder.build().post()
                            .uri("http://" + host + "/api/setup/getInstitutionByCode")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .bodyValue(request)
                            .retrieve()
                            .bodyToMono(InstitutionRequest.class)
                            .block();

    }

    public void getApplicableGradingSetting(AcademicReportRequest ar){
        fetchSetupdata(ar.getInstitutionCode());
        fetchStudents(ar.getInstitutionCode());
        fetchClassGroups(ar.getInstitutionCode());
        gs=singleInstitutionGlobalRequest.getGradingSetting().stream().filter(gs->gs.getId().equals(ar.getGradingSetting())).findFirst().get();
    }

    // @Bean
    public void fetchStudents(String institutionCode) {
         SingleStringRequest request = SingleStringRequest.builder()
                 .val(institutionCode)
                 .build();
        studentsGlobalRequest = webClientBuilder.build().post()
                .uri("http://"+host+"/api/administration-pta/getStudentsByInstitution")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Students>>() {}).block();
         log.info("Global Students List populated with {} records", studentsGlobalRequest.size());
    }
   // @Bean
    public void fetchClassGroups(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();
        lookUpGlobalResponse = webClientBuilder.build().post()
                .uri("http://"+host+"/api/setup/getAllLookUp")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LookupResponse>>() {}).block();
        log.info("Global Lookup List populated with {} records", lookUpGlobalResponse.size());
    }

    private Double computeExamsScore(Double total, Double actual) {
        return (Math.round(((actual / total) * gs.getExamsPercentage())* 100.0) / 100.0);
    }

    private Double computeClassScore(Double total, Double actual) {
        return (Math.round(((actual / total) * gs.getClassPercentage())* 100.0) / 100.0);
    }

    private GradingRequest computeGrade(Double studentScore) {
        return gs.getGradingList()
                .stream()
                .filter(gr -> gr.getLowerLimit() <= studentScore) // Keep grades that apply
                .max(Comparator.comparing(GradingRequest::getLowerLimit)) // Get the one with the highest lower limit
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
                .toList();
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

    public TerminalReportResponse getTerminalReportResponse(List<AssessmentResponse> assessmentResponses,AcademicReportRequest terminalReportRequest) {
        if (singleInstitutionGlobalRequest==null) {
            fetchSetupdata(terminalReportRequest.getInstitutionCode());
            fetchStudents(terminalReportRequest.getInstitutionCode());
            fetchClassGroups(terminalReportRequest.getInstitutionCode());
        }
      return  buildTerminalReportResponse(assessmentResponses,studentsGlobalRequest);
    }

    public TerminalReportResponse getTerminalReportResponseWithParent(List<AssessmentResponse> assessmentResponses,AcademicReportRequest terminalReportRequest) {
        if (singleInstitutionGlobalRequest==null) {
            fetchSetupdata(terminalReportRequest.getInstitutionCode());
            fetchStudents(terminalReportRequest.getInstitutionCode());
            fetchClassGroups(terminalReportRequest.getInstitutionCode());
        }
        return  buildTerminalReportResponseWithParent(assessmentResponses,studentsGlobalRequest);
    }

    public TerminalReportResponse getTerminalReportResponse(List<AssessmentResponse> assessmentResponses, SingleStringRequest studId) {
        Students stud=studentsGlobalRequest.stream().filter(s->s.getStudentId().equalsIgnoreCase(studId.getVal())).findFirst().get();
        if (singleInstitutionGlobalRequest==null) {
            fetchSetupdata(stud.getInstitutionCode());
            fetchStudents(stud.getInstitutionCode());
            fetchClassGroups(stud.getInstitutionCode());
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
                .toList()
                : new ArrayList<>();

        // Step 3: Build TerminalReportResponse
        return TerminalReportResponse.builder()
                .institutionDetail(buildReportInstitutionResponse()) // Assuming this method is implemented
                .studentReportResponseList(studentReports)
                .build();
    }

    public TerminalReportResponse buildTerminalReportResponseWithParent(List<AssessmentResponse> assessmentResponses, List<Students> studentsGlobalRequest) {

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
                .map(student -> mapAssessmentResponse_ToStudentReportResponse_withParent(student, groupedAssessments.get(student.getStudentId())))
                .toList()
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
                .headSignature(singleInstitutionGlobalRequest.getHeadSignature())
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
                .classScore(nullSafeToString(a.getClassScore()))
                .examsScore(nullSafeToString(a.getExamsScore()))
                .totalScore(nullSafeToString(a.getTotalScore()))
                .institutionCode(a.getInstitutionCode())
                .grade(a.getGrade())
                .gradeRemarks(a.getGradeRemarks())
                .position(a.getPosition().toString())
                .build();
    }


    public AssessmentResponse mapAssessment_ToAssessmentResponse_For_Broadsheet(Assessment a,String classGroup) {
        cg=classGroup;
        return AssessmentResponse.builder()
                .academicYear(a.getAcademicYear())
                .dateTime(a.getDateTime())
                .studentClass(a.getStudentClass())
                .term(a.getTerm())
                .studentId(a.getStudentId())
                .subject(a.getSubject())
                .classScore(nullSafeToString(a.getClassScore()))
                .examsScore(nullSafeToString(a.getExamsScore()))
                .totalScore(nullSafeToString(a.getTotalScore()))
                .institutionCode(a.getInstitutionCode())
                .build();
    }

    public static String nullSafeToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }


    public AssessmentResponse mapAssessment_ToAssessmentResponse(Assessment a) {
        return AssessmentResponse.builder()
                .academicYear(a.getAcademicYear())
                .dateTime(a.getDateTime())
                .studentClass(a.getStudentClass())
                .term(a.getTerm())
                .studentId(a.getStudentId())
                .subject(a.getSubject())
                .classScore(nullSafeToString(a.getClassScore()))
                .examsScore(nullSafeToString(a.getExamsScore()))
                .totalScore(nullSafeToString(a.getTotalScore()))
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


    public StudentReportResponse mapAssessmentResponse_ToStudentReportResponse_withParent(Students student, List<AssessmentResponse> assessments) {
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
                .studentParents(student.getStudentParents())
                .build();
    }

/*
* This Code here is not done It needs serious review
* */
    public ExistingUploadedScoreResponse mapAssessment_To_ExistingUploadedScoreResponse(Assessment eus) {
        return ExistingUploadedScoreResponse.builder()
                .classScore(eus.getClassScore())
                .examsScore(eus.getExamsScore())
                .institutionCode(eus.getInstitutionCode())
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

    /*
    public List<Assessment> passExamsAssessment(Map.Entry<String, Map<Long, Map<String, StudentScores>>> sr,AcademicReportRequest terminalReportRequest) {
        String studentClass = sr.getKey(); // Example: "ClassA"
        if (singleInstitutionGlobalRequest==null) {
            fetchSetupdata(terminalReportRequest.getInstitutionCode());
            fetchStudents(terminalReportRequest.getInstitutionCode());
            fetchClassGroups(terminalReportRequest.getInstitutionCode());
        }
        //singleInstitutionGlobalRequest=institutionGlobalRequest.stream().filter(i->i.getBececode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).findFirst().get();
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
                .toList(); // Collect all assessments
    }*/

    public List<Assessment> passExamsAssessment(
            Map.Entry<String, Map<Long, Map<String, StudentScores>>> classEntry,
            AcademicReportRequest request) {

        if (singleInstitutionGlobalRequest == null) {
            fetchSetupdata(request.getInstitutionCode());
            fetchStudents(request.getInstitutionCode());
            fetchClassGroups(request.getInstitutionCode());
        }

        return classEntry.getValue().entrySet().stream()
                .flatMap(subjectEntry -> {
                    Long subjectId = subjectEntry.getKey();

                    // Using SubjectRequest to get subject name
                    String subjectName = singleInstitutionGlobalRequest.getSubjectList().stream()
                            .filter(Objects::nonNull)  // Filter out null subjects
                            .filter(s -> subjectId.equals(s.getId()))  // Match by ID
                            .findFirst()
                            .map(SubjectRequest::getName)  // Get name from SubjectRequest
                            .orElse("UNKNOWN_SUBJECT");  // Default if not found

                    return subjectEntry.getValue().entrySet().stream()
                            .map(studentEntry -> {
                                String studentId = studentEntry.getKey();
                                StudentScores scores = studentEntry.getValue();

                                // Calculate exam score (preserving null)
                                Double examScore = null;
                                if (scores.getTotalScoreObtained() != null &&
                                        scores.getTotalScorePossible() != null &&
                                        scores.getTotalScorePossible() != 0) {
                                    examScore = Math.round(
                                            computeExamsScore(scores.getTotalScorePossible(),scores.getTotalScoreObtained()) * 100)/ 100.0;
                                }

                                return Assessment.builder()
                                        .academicYear(request.getAcademicYear())
                                        .dateTime(LocalDateTime.now())
                                        .studentClass(classEntry.getKey())
                                        .term(request.getTerm())
                                        .studentId(studentId)
                                        .subject(subjectName)
                                        .examsScore(examScore)
                                        .institutionCode(request.getInstitutionCode())
                                        .build();
                            });
                })
                .toList();
    }
   /* public List<Assessment> passContinuousAssessment(Map.Entry<String, Map<Long, Map<String, StudentScores>>> sr, AcademicReportRequest terminalReportRequest, StudentScores studentScores) {
        String studentClass = sr.getKey(); // Example: "ClassA"
        if (singleInstitutionGlobalRequest==null) {
            fetchSetupdata(terminalReportRequest.getInstitutionCode());
            fetchStudents(terminalReportRequest.getInstitutionCode());
            fetchClassGroups(terminalReportRequest.getInstitutionCode());
        }
        //singleInstitutionGlobalRequest=institutionGlobalRequest.stream().filter(i->i.getBececode().equalsIgnoreCase(terminalReportRequest.getInstitutionCode())).findFirst().get();

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

                                        .institutionCode(terminalReportRequest.getInstitutionCode())
                                        //.grade(computeGrade(scores.getTotalScoreObtained() + scores.getTotalScorePossible()).getGrade())
                                        .build();
                            });
                })
                .toList(); // Collect all assessments
    }*/

    public List<Assessment> passContinuousAssessment(
            Map.Entry<String, Map<Long, Map<String, StudentScores>>> classEntry,
            AcademicReportRequest request,
            StudentScores highestScores) {

        if (singleInstitutionGlobalRequest == null) {
            fetchSetupdata(request.getInstitutionCode());
            fetchStudents(request.getInstitutionCode());
            fetchClassGroups(request.getInstitutionCode());
        }

        return classEntry.getValue().entrySet().stream()
                .flatMap(subjectEntry -> {
                    Long subjectId = subjectEntry.getKey();

                    // Using SubjectRequest DTO to get subject name
                    String subjectName = singleInstitutionGlobalRequest.getSubjectList().stream()
                            .filter(Objects::nonNull)  // Filter out null subjects
                            .filter(s -> subjectId.equals(s.getId()))  // Match by ID
                            .findFirst()
                            .map(SubjectRequest::getName)  // Get name from SubjectRequest
                            .orElse("UNKNOWN_SUBJECT");  // Default if not found

                    return subjectEntry.getValue().entrySet().stream()
                            .map(studentEntry -> {
                                String studentId = studentEntry.getKey();
                                StudentScores scores = studentEntry.getValue();

                                // Calculate class score (preserving null)
                                Double classScore = null;
                                if (scores.getTotalScoreObtained() != null &&
                                        highestScores.getTotalScorePossible() != null &&
                                        highestScores.getTotalScorePossible() != 0) {
                                    classScore = Math.round(
                                            computeClassScore(highestScores.getTotalScorePossible(),scores.getTotalScoreObtained()) * 100)/ 100.0;
                                }

                                // Build assessment with potential null values
                                Assessment assessment = Assessment.builder()
                                        .academicYear(request.getAcademicYear())
                                        .dateTime(LocalDateTime.now())
                                        .studentClass(classEntry.getKey())
                                        .term(request.getTerm())
                                        .studentId(studentId)
                                        .subject(subjectName)
                                        .classScore(classScore)  // Could be null
                                        .institutionCode(request.getInstitutionCode())
                                        .build();

                                // Mark assessments with null data
                                if (classEntry.getKey().equals("NULL_CLASS") ||
                                        subjectId == -1L ||
                                        studentId.equals("NULL_STUDENT_ID") ||
                                        scores.hasNullData()) {
                                    assessment.setGradeRemarks("INCOMPLETE_DATA");
                                }

                                return assessment;
                            });
                })
                .toList();
    }


    public void sendSMS(List<StudentReportResponse> studentReports, String intitution){

        StringBuilder report = new StringBuilder();

        // Iterate over each student report
        for (StudentReportResponse studentReport : studentReports) {
            // Basic report structure
            StringBuilder studentReportString = new StringBuilder("Dear parent, end of ");

            // Assuming the first assessmentResponse's term is what you want (same for all assessments)
            if (studentReport.getStudentAssessment() != null && !studentReport.getStudentAssessment().isEmpty()) {
                String term = studentReport.getStudentAssessment().get(0).getTerm();
                studentReportString.append(term).append(" report for ");
            }

            // Add last name
            studentReportString.append(studentReport.getLastName()).append(" => ");

            // Add subjects and grades
            StringBuilder subjectsAndGrades = new StringBuilder();
            for (AssessmentResponse assessment : studentReport.getStudentAssessment()) {
                subjectsAndGrades.append(assessment.getSubject())
                        .append(": ")
                        .append(assessment.getGrade())
                        .append(" ");
            }

            // Append subjects and grades to the report
            studentReportString.append(subjectsAndGrades.toString());

            // Add overall position and grade
            studentReportString.append("|| Overall Position: ")
                    .append(studentReport.getAveragePosition())
                    .append(" Overall Grade: ")
                    .append(studentReport.getAverageGrade());

            // Append the result to the main report string
            report.append(studentReportString.toString()).append("\n");
            List<Parents> sp= studentReport.getStudentParents();
             sp.forEach (p-> {
                try {
                    Future<String> futureResult =  asyncGet(p.getContact1().trim(),studentReportString.toString(),intitution);
                  String result= futureResult.get();
                    log.info("Result from asyncGet: " +result);
                } catch (Exception e) {
                    log.error("Error during async processing", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }






    public Future<String> asyncGet(String parentContact, String message,String institution) throws Exception
    {
        CompletableFuture<String> completableFuture = new CompletableFuture<String>();
        executorService.execute(() -> {
            String uri;
            try {
                String URIMessage= URLEncoder.encode(( message), "UTF-8");
                uri = "https://sms.arkesel.com/sms/api?action=send-sms&api_key=aXR0ZE9nblRHQWx4UmlHenFnVVU&to="
                        + parentContact.trim() + "&from="+ getInitials(institution) +"&sms=" + URIMessage;

                URL url = new URL(uri);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();
                Integer status = urlConnection.getResponseCode();
                InputStream inputStream;
                if (status != HttpURLConnection.HTTP_OK)
                    inputStream = urlConnection.getErrorStream();
                else
                    inputStream = urlConnection.getInputStream();

                urlConnection.disconnect();
                completableFuture.complete(status.toString());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    public static String getInitials(String text) {
        // Split the text by spaces
        String[] words = text.split(" ");
        StringBuilder initials = new StringBuilder();

        // Loop through each word and take the first letter
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));  // Append the first character of each word
            }
        }

        // Return the initials as a string
        return initials.toString().toUpperCase();  // Convert to uppercase for standard initials format
    }
}
