package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.request.alien.ApplicationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.*;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.model.StudentAccount;
import com.astromyllc.shootingstar.adminpta.model.StudentSubjects;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import com.astromyllc.shootingstar.adminpta.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentUtil {

    // Key format: institutionCode:studentClass
    public static final Map<String, List<Students>> indexedStudents = new ConcurrentHashMap<>();
    public static final Map<String, List<Students>> contactIndex = new ConcurrentHashMap<>();
    public static List<Students> studentsGlobalList;
    public static List<Parents> parentsGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final StudentSubjectUtil studentSubjectUtil;
    private final StudentAccountUtil studentAccountUtil;
    private final ParentsUtil parentsUtil;
    private final WebClient.Builder webClientBuilder;
    long studentCount = 0L;
    int cnt = 1;
    @Value("${gateway.host}")
    private String host;

    private static void indexContact(String contact, String studentId) {
        if (contact == null || contact.trim().isEmpty()) return;

        String key = contact.trim().toLowerCase();
        contactIndex.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>())
                .add(findStudentById(studentId));
    }

    private static Students findStudentById(String studentId) {
        return studentsGlobalList.stream()
                .filter(s -> studentId.equals(s.getStudentId()))
                .findFirst()
                .orElse(null);
    }

    public static List<Students> findStudentsByContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String key = contact.trim().toLowerCase();
        List<Students> results = contactIndex.getOrDefault(key, Collections.emptyList());

        // Return defensive copy
        return new ArrayList<>(results);
    }

    @PostConstruct
    private void fetAllStudents() {
        studentsGlobalList = studentRepository.findStudentsByStatus("Active");
        parentsGlobalList = parentRepository.findAll();
        log.info("Global Students List populated with {} records", studentsGlobalList.size());
        indexStudents(StudentUtil.studentsGlobalList);


        parentsGlobalList.parallelStream().forEach(parent -> {
            indexContact(parent.getContact1(), parent.getStudentId());
            indexContact(parent.getContact2(), parent.getStudentId());
            indexContact(parent.getEmail(), parent.getStudentId());
        });
    }

    public void indexStudents(List<Students> students) {
        students.forEach(student -> {
            String key = student.getInstitutionCode().toLowerCase() + ":" + student.getStudentClass().toLowerCase();
            indexedStudents.computeIfAbsent(key, k -> new ArrayList<>()).add(student);
        });
    }

    private List<ApplicationRequest> fetchStudents(AdmissionRequest admissionRequest) {
        JSONObject json = new JSONObject();
        json.put("institutionCode", admissionRequest.getInstitutionCode());
        json.put("applicationDate", admissionRequest.getApplicationDate());
        json.put("applicationStatus", admissionRequest.getApplicationStatus());
        log.info(json.toJSONString());
        return webClientBuilder.build().post()
                .uri(host + "/api/applications/getProcessedApplicationsBySchool")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(json), JSONObject.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ApplicationRequest>>() {
                }).block();
    }

    private Student_BillResponse fetchStudentsBalance(StudentBillFetchRequest billFetchRequest) {
        JSONObject json = new JSONObject();
        json.put("institutionCode", billFetchRequest.getInstitutionCode());
        json.put("studentClass", billFetchRequest.getStudentClass());
        json.put("studentId", billFetchRequest.getStudentId());
        log.info(json.toJSONString());
        return webClientBuilder.build().post()
                .uri(host + "/api/finance/getStudentBillByIdAndInstitution")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(json), JSONObject.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Student_BillResponse>() {
                }).block();
    }

    public void getCurrentApplications(AdmissionRequest admissionRequest) {
        fetchStudents(admissionRequest).stream().map(this::mapAdmittedStudents).toList();
    }

    private String mapAdmittedStudents(ApplicationRequest s) {
        String d = Integer.toString(LocalDate.now().getYear()).substring(2);
        String studentId = generateStudentId(s.getApplicationInstitution(), d);
        List<Students> studentsList = new ArrayList<>();
        studentsList.add(Students.builder()
                .institutionCode(s.getApplicationInstitution())
                .studentId(studentId)
                .birthCert(s.getApplicantBirthCert())
                .countryOfBirth(s.getApplicantCountryOfBirth())
                .dateOfAdmission(LocalDate.now())
                .dateOfBirth(s.getApplicantDateOfBirth())
                .lastName(s.getApplicantLastName())
                .denomination(s.getApplicantDenomination())
                .firstName(s.getApplicantFirstName())
                .gender(s.getApplicantGender())
                .residentialLocality(s.getApplicantResidencetialLocality())
                .otherName(s.getApplicantOtherName())
                .picture(s.getApplicantPicture())
                .placeOfBirth(s.getApplicantPlaceOfBirth())
                .status(s.getApplicationStatus())
                .build());

        List<Parents> parentsList = new ArrayList<>();
        parentsList.addAll(s.getParentsRequests().stream().map(sp -> mapStudentParent(sp, studentId)).toList());

        try {
            parentsUtil.bulkCreateKeycloakUsers(parentsList.stream().map(parentsUtil::mapParent_ToParentRequest).toList());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        studentRepository.saveAll(studentsList);
        parentRepository.saveAll(parentsList);

        studentsGlobalList.addAll(studentsList);
        parentsGlobalList.addAll(parentsList);
        log.info("{} records have been added to the students list", studentsGlobalList.size());
        return "done";
    }

    private Parents mapStudentParent(ParentsRequest sp, String studentId) {
        log.info("{} Student - {} parent {}", cnt, studentId, sp.getContact1());
        cnt += 1;
        return Parents.builder()
                .email(sp.getEmail())
                .studentId(studentId)
                .institutionCode(sp.getInstitutionCode())
                .lastName(sp.getLastName())
                .parentType(sp.getParentType())
                .contact1(sp.getContact1())
                .contact2(sp.getContact2())
                .occupation(sp.getOccupation())
                .firstNames(sp.getFirstNames())
                .placeOfWork(sp.getPlaceOfWork())
                .build();
    }

    private String generateStudentId(String applicationInstitution, String academicYear) {
        if (studentsGlobalList.size() > 0 && studentCount < 1) {
            studentCount = 1 + studentsGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(applicationInstitution)
                    && Integer.toString(s.getDateOfAdmission().getYear()).substring(2).equalsIgnoreCase(academicYear)).count();
        } else {
            studentCount += 1;
        }
        String id = applicationInstitution + StringUtils.right(("00000" + studentCount), 5) + academicYear;
        return id;
    }

    public StudentsResponse mapStudent_ToStudentResponse(Students s) {

        List<ParentsResponse> p = parentsGlobalList.parallelStream()
                .filter(sp -> sp.getStudentId().equalsIgnoreCase(s.getStudentId()))
                .map(this::mapParent_ToParentResponse)
                .toList();

        List<StudentSubjectsResponse> ss = Optional.ofNullable(s.getStudentSubjects())
                .orElse(Collections.emptyList())
                .stream()
                .map(StudentSubjectUtil::mapStudentSubject_ToStudentSubjectResponse)
                .toList();

        return StudentsResponse.builder()
                .institutionCode(s.getInstitutionCode())
                .studentId(s.getStudentId())
                .birthCert(s.getBirthCert())
                .countryOfBirth(s.getCountryOfBirth())
                .dateOfAdmission(s.getDateOfAdmission())
                .dateOfBirth(s.getDateOfBirth())
                .lastName(s.getLastName())
                .denomination(s.getDenomination())
                .firstName(s.getFirstName())
                .gender(s.getGender())
                .nationality(s.getResidentialLocality())
                .otherName(s.getOtherName())
                .picture(s.getPicture())
                .placeOfBirth(s.getPlaceOfBirth())
                .status(s.getStatus())
                .residentialLocality(s.getResidentialLocality())
                .studentClass(s.getStudentClass())
                .studentParents(p)
                .studentSubjectsResponse(ss)
                .build();
    }

    public StudentSkimResponse mapStudent_ToSkimpStudentResponse(Students s) {
        return StudentSkimResponse.builder()
                .institutionCode(s.getInstitutionCode())
                .studentId(s.getStudentId())
                .dateOfAdmission(String.valueOf(s.getDateOfAdmission()))
                .lastName(s.getLastName())
                .firstName(s.getFirstName())
                .gender(s.getGender())
                .nationality(s.getResidentialLocality())
                .otherName(s.getOtherName())
                .picture(s.getPicture())
                .status(s.getStatus())
                .studentClass(s.getStudentClass())
                .build();
    }

    public StudentStatusResponse mapStudent_ToStudentStatusResponse(Students s) {
        Double billBalance = fetchStudentsBalance(new StudentBillFetchRequest(
                s.getInstitutionCode(),
                s.getStudentId(),
                s.getStudentClass()
        )).getAmountBalance();
        return StudentStatusResponse.builder()
                .institutionCode(s.getInstitutionCode())
                .studentId(s.getStudentId())
                .dateOfAdmission(String.valueOf(s.getDateOfAdmission()))
                .lastName(s.getLastName())
                .firstName(s.getFirstName())
                .gender(s.getGender())
                .nationality(s.getResidentialLocality())
                .otherName(s.getOtherName())
                .picture(s.getPicture())
                .status(s.getStatus())
                .studentClass(s.getStudentClass())
                .feeBalance(billBalance.toString())
                .assignedRouteId(s.getAssignedRouteId())
                .assignedRouteName(s.getAssignedRouteName())
                .build();
    }

    // Sets (or clears, if request.getRouteId() is null) the student's
    // assigned transport route and persists it. studentsGlobalList holds
    // this same object reference, so the cache stays correct in place.
    public Students applyRouteAssignment(Students student, SetStudentRouteRequest request) {
        student.setAssignedRouteId(request.getRouteId());
        student.setAssignedRouteName(request.getRouteName());
        studentRepository.save(student);
        return student;
    }

    public StudentSkimWithParentResponse mapStudent_ToStudentSkimWithParentResponse(Students s) {

        List<ParentsResponse> p = parentsGlobalList.parallelStream()
                .filter(sp -> sp.getStudentId().equalsIgnoreCase(s.getStudentId()))
                .map(this::mapParent_ToParentResponse)
                .toList();

        List<StudentAccountResponse> a = StudentAccountUtil.studentAccountsGlobalList.parallelStream()
                .filter(sa -> sa.getStudentId().equalsIgnoreCase(s.getStudentId()))
                .map(StudentAccountUtil::mapStudentAccount_ToStudentAccountResponse)
                .toList();

        InstitutionRequest ir = parentsUtil.getSkimpInstitution(s.getInstitutionCode());
        return StudentSkimWithParentResponse.builder()
                .institutionCode(ir.getBececode())
                .institutionName(ir.getName())
                .studentId(s.getStudentId())
                .dateOfAdmission(String.valueOf(s.getDateOfAdmission()))
                .lastName(s.getLastName())
                .firstName(s.getFirstName())
                .gender(s.getGender())
                .nationality(s.getCountryOfBirth())
                .denomination(s.getDenomination())
                .residentialLocality(s.getResidentialLocality())
                .otherName(s.getOtherName())
                .picture(s.getPicture())
                .status(s.getStatus())
                .studentClass(s.getStudentClass())
                .parents(p)
                .studentAccount(a)
                .build();
    }

    public ParentsResponse mapParent_ToParentResponse(Parents p) {
        return ParentsResponse.builder()
                .id(p.getId() != null ? p.getId().toString() : null)
                .studentId(p.getStudentId())
                .institutionCode(p.getInstitutionCode())
                .parentType(p.getParentType())
                .firstNames(p.getFirstNames())
                .lastName(p.getLastName())
                .contact1(p.getContact1())
                .contact2(p.getContact2())
                .email(p.getEmail())
                .occupation(p.getOccupation())
                .placeOfWork(p.getPlaceOfWork())
                .build();
    }

    public ClassListResponse mapStudent_ToClassListResponse(Students s) {
        return ClassListResponse.builder()
                .Name(s.getLastName() + " " + s.getFirstName() + (s.getOtherName() != null && !s.getOtherName().isEmpty() ? " " + s.getOtherName() : ""))
                .StudentID(s.getStudentId())
                .StudentClass(s.getStudentClass())
                .Score("")
                .Subject("")
                .TotalScore("")
                .build();
    }

    public Students mapStudentsRequest_To_Students(StudentsImportRequest s) {
        String studentId = s.getStudentId().trim().isEmpty() ? generateStudentId(s.getInstitutionCode(), Integer.toString(s.getDateOfAdmission().getYear()).substring(2)) : s.getStudentId();

        List<Parents> parentsList = new ArrayList<>();
        return Students.builder()
                .studentId(studentId)
                .dateOfAdmission(s.getDateOfAdmission())
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .firstName(s.getFirstName())
                .denomination(s.getDenomination())
                .otherName(s.getOtherName())
                .lastName(s.getLastName())
                .studentClass(s.getStudentClass())
                .status(s.getStatus())
                .placeOfBirth(s.getPlaceOfBirth())
                .countryOfBirth(s.getCountryOfBirth())
                .residentialLocality(s.getResidentialLocality())
                .institutionCode(s.getInstitutionCode())
                .picture(s.getPicture())
                .birthCert(s.getBirthCert())
                .parentsList(parentsList)
                .studentSubjects(new ArrayList<>())
                .studentAccount(new ArrayList<>())
                .build();
    }

    public Students mapStudentsRequest_To_Students(Students2Request s) {
        String studentId = s.getStudentId().trim().isEmpty() ? generateStudentId(s.getInstitutionCode(), Integer.toString(s.getDateOfAdmission().getYear()).substring(2)) : s.getStudentId();

        List<Parents> parentsList = new ArrayList<>();
        return Students.builder()
                .studentId(studentId)
                .dateOfAdmission(s.getDateOfAdmission())
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .firstName(s.getFirstName())
                .denomination(s.getDenomination())
                .otherName(s.getOtherName())
                .lastName(s.getLastName())
                .studentClass(s.getStudentClass())
                .status(s.getStatus())
                .placeOfBirth(s.getPlaceOfBirth())
                .countryOfBirth(s.getCountryOfBirth())
                .residentialLocality(s.getResidentialLocality())
                .institutionCode(s.getInstitutionCode())
                .picture(s.getPicture())
                .birthCert(s.getBirthCert())
                .parentsList(parentsList)
                .build();
    }

    public Students mapStudentsRequest_To_Students(StudentsImportRequest s, Students es) {
        if (es.getStudentId().trim().isEmpty()) {
            String studentId = s.getStudentId().trim().isEmpty() ? generateStudentId(s.getInstitutionCode(), Integer.toString(s.getDateOfAdmission().getYear()).substring(2)) : s.getStudentId();

            List<Parents> parentsList = new ArrayList<>();

            parentsList.addAll(
                    s.getParentsRequests().stream()
                            .map(parentRequest -> mapStudentParent(parentRequest, studentId))
                            .toList()
            );

            try {
                parentsUtil.bulkCreateKeycloakUsers(parentsList.stream().map(parentsUtil::mapParent_ToParentRequest).toList());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            parentRepository.saveAll(parentsList);
            return Students.builder()
                    .studentId(studentId)
                    .dateOfAdmission(s.getDateOfAdmission())
                    .dateOfBirth(s.getDateOfBirth())
                    .gender(s.getGender())
                    .firstName(s.getFirstName())
                    .denomination(s.getDenomination())
                    .otherName(s.getOtherName())
                    .lastName(s.getLastName())
                    .studentClass(s.getStudentClass())
                    .status(s.getStatus())
                    .placeOfBirth(s.getPlaceOfBirth())
                    .countryOfBirth(s.getCountryOfBirth())
                    .residentialLocality(s.getResidentialLocality())
                    .institutionCode(s.getInstitutionCode())
                    .picture(s.getPicture())
                    .birthCert(s.getBirthCert())
                    .parentsList(parentsList)
                    .studentSubjects(new ArrayList<>())
                    .studentAccount(new ArrayList<>())
                    .build();

        } else {
            // Always use the existing student's ID
            String studentId = es.getStudentId();

            // Update the existing student's fields instead of creating new ones
            // Only fields that are non-null in the incoming request are updated -
            // null means "leave as-is", not "clear this field".
            if (s.getFirstName() != null && !s.getFirstName().isBlank()) es.setFirstName(s.getFirstName());
            if (s.getOtherName() != null && !s.getOtherName().isBlank()) es.setOtherName(s.getOtherName());
            if (s.getLastName() != null && !s.getLastName().isBlank()) es.setLastName(s.getLastName());
            if (s.getGender() != null && !s.getGender().isBlank()) es.setGender(s.getGender());
            if (s.getDateOfBirth() != null) es.setDateOfBirth(s.getDateOfBirth());
            if (s.getPlaceOfBirth() != null && !s.getPlaceOfBirth().isBlank()) es.setPlaceOfBirth(s.getPlaceOfBirth());
            if (s.getCountryOfBirth() != null && !s.getCountryOfBirth().isBlank())
                es.setCountryOfBirth(s.getCountryOfBirth());
            if (s.getDateOfAdmission() != null) es.setDateOfAdmission(s.getDateOfAdmission());
            if (s.getResidentialLocality() != null && !s.getResidentialLocality().isBlank())
                es.setResidentialLocality(s.getResidentialLocality());
            if (s.getStudentClass() != null && !s.getStudentClass().isBlank()) es.setStudentClass(s.getStudentClass());
            if (s.getDenomination() != null && !s.getDenomination().isBlank()) es.setDenomination(s.getDenomination());
            if (s.getStatus() != null && !s.getStatus().isBlank()) es.setStatus(s.getStatus());
            if (s.getInstitutionCode() != null && !s.getInstitutionCode().isBlank())
                es.setInstitutionCode(s.getInstitutionCode());
            if (s.getPicture() != null && !s.getPicture().isBlank()) es.setPicture(s.getPicture());
            if (s.getBirthCert() != null && !s.getBirthCert().isBlank()) es.setBirthCert(s.getBirthCert());

            return es;
        }
    }

    public Students mapStudentsRequest_To_Students(Students2Request s, Students es) throws IOException {
        if (es.getStudentId().trim().isEmpty()) {
            String studentId = s.getStudentId().trim().isEmpty() ? generateStudentId(s.getInstitutionCode(), Integer.toString(s.getDateOfAdmission().getYear()).substring(2)) : s.getStudentId();

            List<Parents> parentsList = new ArrayList<>(s.getStudentParents().stream()
                    .map(parentRequest -> mapStudentParent(parentRequest, studentId))
                    .toList());

            try {
                parentsUtil.bulkCreateKeycloakUsers(parentsList.stream().map(parentsUtil::mapParent_ToParentRequest).toList());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            parentRepository.saveAll(parentsList);

            List<StudentSubjects> studentSubjectsList = new ArrayList<>(s.getStudentSubjectsList().stream()
                    .map(subjectRequest -> StudentSubjectUtil.mapStudentsSubjectRequest_ToStudentsSubjects(subjectRequest, studentId))
                    .toList());
            studentSubjectUtil.saveAll(studentSubjectsList);

            return Students.builder()
                    .studentId(studentId)
                    .dateOfAdmission(s.getDateOfAdmission())
                    .dateOfBirth(s.getDateOfBirth())
                    .gender(s.getGender())
                    .firstName(s.getFirstName())
                    .denomination(s.getDenomination())
                    .otherName(s.getOtherName())
                    .lastName(s.getLastName())
                    .studentClass(s.getStudentClass())
                    .status(s.getStatus())
                    .placeOfBirth(s.getPlaceOfBirth())
                    .countryOfBirth(s.getCountryOfBirth())
                    .residentialLocality(s.getResidentialLocality())
                    .institutionCode(s.getInstitutionCode())
                    .picture(Base64.getEncoder().encodeToString(processAndValidateImage(s.getPicture(), 150, 256)))
                    .birthCert(s.getBirthCert())
                    .parentsList(parentsList)
                    .studentSubjects(studentSubjectsList)
                    .studentAccount(new ArrayList<>())
                    .build();

        } else {
            // Always use the existing student's ID
            String studentId = es.getStudentId();

            // Update the existing student's fields instead of creating new ones
            es.setGender(s.getGender());
            es.setFirstName(s.getFirstName());
            es.setOtherName(s.getOtherName());
            es.setLastName(s.getLastName());
            es.setDateOfAdmission(s.getDateOfAdmission());
            es.setDateOfBirth(s.getDateOfBirth());
            es.setStatus(s.getStatus());
            es.setDenomination(s.getDenomination());
            es.setStudentClass(s.getStudentClass());
            es.setPlaceOfBirth(s.getPlaceOfBirth());
            es.setCountryOfBirth(s.getCountryOfBirth());
            es.setResidentialLocality(s.getResidentialLocality());
            es.setInstitutionCode(s.getInstitutionCode());
            es.setPicture(s.getPicture());
            es.setBirthCert(s.getBirthCert());

            return es;
        }
    }

    public Optional<StudentsResponse> createNewStudents(StudentsImportRequest studentsRequest) throws URISyntaxException, IOException {
        Students newStudents = mapStudentsRequest_To_Students(studentsRequest);

        processRecords(studentsRequest.getParentsRequests(),
                (r -> ParentsUtil.mapParentRequest_ToParent(r, newStudents.getStudentId())),
                parentsUtil::saveAll,
                newStudents::setParentsList);

        processRecords(studentsRequest.getStudentSubjectsRequests(),
                r -> StudentSubjectUtil.mapStudentsSubjectRequest_ToStudentsSubjects(r, newStudents.getStudentId()),
                studentSubjectUtil::saveAll,
                newStudents::setStudentSubjects);

        studentRepository.save(newStudents);
        studentsGlobalList.add(newStudents);

        return Optional.of(mapStudent_ToStudentResponse(newStudents));
    }

    public Optional<StudentsResponse> createNewStudents(Students2Request studentsRequest) throws URISyntaxException, IOException {
        Students newStudents = mapStudentsRequest_To_Students(studentsRequest);
        List<Parents> parents = new ArrayList<>();

        studentsRequest.getStudentParents().forEach(r -> {
            Parents parent = ParentsUtil.mapParentRequest_ToParent(r, newStudents.getStudentId());
            parentsUtil.KeyclaokCreateUserCredentials(r);
            parents.add(parent);
        });

        if (!parents.isEmpty()) {
            parentsUtil.saveAll(parents);
            newStudents.getParentsList().addAll(parents);
        }

        /*processRecords(studentsRequest.getStudentSubjectsList(),
                r -> StudentSubjectUtil.mapStudentsSubjectRequest_ToStudentsSubjects(r, newStudents.getStudentId()),
                studentSubjectUtil::saveAll,
                newStudents::setStudentSubjects);*/

        studentRepository.save(newStudents);
        studentsGlobalList.add(newStudents);

        return Optional.of(mapStudent_ToStudentResponse(newStudents));
    }

    public Optional<StudentsResponse> updateExistingStudents(StudentsImportRequest studentsImportRequest, Students existingStudent)
            throws URISyntaxException, IOException {

        // 1. Update basic student info (preserving the same studentId)
        Students updatedStudents = mapStudentsRequest_To_Students(studentsImportRequest, existingStudent);

        List<Parents> existingParents = StudentUtil.parentsGlobalList.stream()
                .filter(p -> p.getStudentId().equalsIgnoreCase(existingStudent.getStudentId()))
                .collect(Collectors.toList());

        // 2. Handle parents - works whether existingStudent.getParentsList() is null/empty or not
        this.<ParentsRequest, Parents>updateRecords(
                studentsImportRequest.getParentsRequests(),
                existingParents,
                ParentsUtil::mapParentRequest_ToParent,
                parentsUtil::updateParents,
                parentsUtil::saveAll,
                updatedStudents::setParentsList,
                updatedStudents.getStudentId(),
                this::parentsMatch
        );

        // 3. Handle Student Account - works whether existingStudent.getParentsList() is null/empty or not
        this.<StudentAccountRequest, StudentAccount>updateRecords(
                studentsImportRequest.getStudentAccountRequests(),
                existingStudent.getStudentAccount() != null ? existingStudent.getStudentAccount() : Collections.emptyList(),
                StudentAccountUtil::mapStudentAccountRequest_ToStudentAccount,
                studentAccountUtil::updateStudentAccount,
                studentAccountUtil::saveAll,
                updatedStudents::setStudentAccount,
                updatedStudents.getStudentId(),
                (ent, req) -> ent.getActivationState().equalsIgnoreCase(req.getActivationState())
        );

        // 4. Handle subjects similarly
        this.<StudentSubjectsRequest, StudentSubjects>updateRecords(
                studentsImportRequest.getStudentSubjectsRequests(),
                existingStudent.getStudentSubjects() != null ? existingStudent.getStudentSubjects() : Collections.emptyList(),
                StudentSubjectUtil::mapStudentsSubjectRequest_ToStudentsSubjects,
                studentSubjectUtil::updateStudentSubjects,
                studentSubjectUtil::saveAll,
                updatedStudents::setStudentSubjects,
                updatedStudents.getStudentId(),
                (ent, req) -> ent.getSubjectName().equalsIgnoreCase(req.getSubjectName())
        );

        studentRepository.save(updatedStudents);
        return Optional.of(mapStudent_ToStudentResponse(updatedStudents));
    }

    public Optional<StudentsResponse> updateExistingStudents(Students2Request studentsImportRequest, Students existingStudent)
            throws URISyntaxException, IOException {

        // 1. Update basic student info (preserving the same studentId)
        Students updatedStudents = mapStudentsRequest_To_Students(studentsImportRequest, existingStudent);

        // 2. Handle parents - works whether existingStudent.getParentsList() is null/empty or not
        this.<ParentsRequest, Parents>updateRecords(
                studentsImportRequest.getStudentParents(),
                existingStudent.getParentsList() != null ? existingStudent.getParentsList() : Collections.emptyList(),
                ParentsUtil::mapParentRequest_ToParent,
                parentsUtil::updateParents,
                parentsUtil::saveAll,
                updatedStudents::setParentsList,
                updatedStudents.getStudentId(),
                this::parentsMatch
        );

        // 4. Handle subjects similarly
        this.<StudentSubjectsRequest, StudentSubjects>updateRecords(
                studentsImportRequest.getStudentSubjectsList(),
                existingStudent.getStudentSubjects() != null ? existingStudent.getStudentSubjects() : Collections.emptyList(),
                StudentSubjectUtil::mapStudentsSubjectRequest_ToStudentsSubjects,
                studentSubjectUtil::updateStudentSubjects,
                studentSubjectUtil::saveAll,
                updatedStudents::setStudentSubjects,
                updatedStudents.getStudentId(),
                (ent, req) -> ent.getSubjectName().equalsIgnoreCase(req.getSubjectName())
        );

        studentRepository.save(updatedStudents);
        return Optional.of(mapStudent_ToStudentResponse(updatedStudents));
    }

    private <T, R> void processRecords(List<T> records,
                                       Function<T, R> mapper,
                                       Consumer<List<R>> saveFn,
                                       Consumer<List<R>> setter) {
        if (records != null && !records.isEmpty()) {
            List<R> mapped = records.stream().map(mapper).collect(Collectors.toList());
            saveFn.accept(mapped);
            setter.accept(mapped);
        }
    }

    private <REQ, ENT> void updateRecords(List<REQ> requestRecords,
                                          List<ENT> existingRecords,
                                          BiFunction<REQ, String, ENT> mapper,
                                          TriConsumer<ENT, REQ, String> updater,
                                          Consumer<List<ENT>> saveFn,
                                          Consumer<List<ENT>> setter,
                                          String studentsCode,
                                          BiPredicate<ENT, REQ> matchPredicate) {
        if (requestRecords != null && !requestRecords.isEmpty()) {
            // Initialize existingRecords to empty list if null
            List<ENT> safeExistingRecords = existingRecords != null ? existingRecords : Collections.emptyList();

            List<ENT> updated = requestRecords.stream()
                    .map(req -> {
                        Optional<ENT> existing = safeExistingRecords.stream()
                                .filter(e -> matchPredicate.test(e, req))
                                .findFirst();
                        return existing.map(e -> {
                            updater.accept(e, req, studentsCode);
                            return e;
                        }).orElseGet(() -> mapper.apply(req, studentsCode));
                    })
                    .collect(Collectors.toList());
            saveFn.accept(updated);
            setter.accept(updated);
        }
    }

    /**
     * Decides whether an incoming ParentsRequest refers to an already-existing Parents
     * record for this student, so partial updates don't get misread as brand-new parents
     * (which is what was causing duplicate parent records).
     * <p>
     * Falls through several identifiers in order of reliability, since most incoming
     * requests won't have an `id` set:
     * 1. id            - exact match, most reliable when the client has it
     * 2. contact1       - phone numbers are effectively unique per person and rarely blank
     * 3. email          - decent fallback when contact1 is missing
     * 4. parentType+firstNames+lastName - last resort, weakest signal, but still better
     *                      than parentType alone (which collides whenever a student has
     *                      two parents of the same type, e.g. two "Biological" parents).
     */
    /**
     * Decides whether an incoming ParentsRequest refers to an already-existing Parents
     * record for this student.
     * <p>
     * Matches on (studentId, firstNames, lastName). This is safe specifically because
     * parent names are NOT editable in this system - unlike email/contact1/parentType,
     * which parents can change, so using them as an identity key would make a legitimate
     * edit look like "this parent is gone, create a new one." Name + studentId is the one
     * combination guaranteed to stay constant across updates.
     * <p>
     * `id` is still checked first when present, since it's an even more exact match and
     * costs nothing extra to check.
     */
    private boolean parentsMatch(Parents ent, ParentsRequest req) {
        if (req.getId() != null && !req.getId().isBlank()) {
            return ent.getId() != null && ent.getId().toString().equalsIgnoreCase(req.getId());
        }

        boolean nameProvided = req.getFirstNames() != null && !req.getFirstNames().isBlank()
                && req.getLastName() != null && !req.getLastName().isBlank();

        if (nameProvided) {
            return ent.getFirstNames() != null && ent.getLastName() != null
                    && ent.getFirstNames().equalsIgnoreCase(req.getFirstNames())
                    && ent.getLastName().equalsIgnoreCase(req.getLastName())
                    && ent.getStudentId() != null && ent.getStudentId().equalsIgnoreCase(req.getStudentId());
        }

        // Name wasn't actually supplied on this request (null OR blank) - the primary
        // key can't be used, so fall back to contact1 as a last resort rather than
        // guaranteeing a duplicate. Less reliable since contact1 can change, but far
        // better than creating a blank-named parent record.
        return req.getContact1() != null && !req.getContact1().isBlank()
                && ent.getContact1() != null
                && ent.getContact1().equalsIgnoreCase(req.getContact1())
                && ent.getStudentId() != null && ent.getStudentId().equalsIgnoreCase(req.getStudentId());
    }

    private boolean isSameRecord(Object existing, Object incoming) {
        if (existing instanceof Parents e && incoming instanceof Parents r) {
            return e.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()) &&
                    e.getStudentId().equals(r.getStudentId());
        } else if (existing instanceof StudentSubjects e && incoming instanceof StudentSubjects r) {
            return e.getSubjectName().equalsIgnoreCase(r.getSubjectName());
        }
        return false;
    }

    public byte[] processAndValidateImage(String clientSideBase64, int maxFileSizeKB, int maxWidth) throws IOException, ValidationException {

        // 1. Decode the client-supplied data
        String base64Data = clientSideBase64.substring(clientSideBase64.indexOf(",") + 1);
        byte[] clientImageBytes = Base64.getDecoder().decode(base64Data);

        // 2. VALIDATE: Basic sanity check on the decoded size
        if (clientImageBytes.length > (maxFileSizeKB * 1024)) {
            throw new ValidationException("Uploaded image is too large after client-side processing.");
        }

        // 3. Read the image into a BufferedImage for inspection
        ByteArrayInputStream bais = new ByteArrayInputStream(clientImageBytes);
        BufferedImage image = ImageIO.read(bais);
        if (image == null) {
            throw new ValidationException("Uploaded data is not a valid image.");
        }

        // 4. VALIDATE: Check dimensions (e.g., prevent a 1x1 pixel image)
        if (image.getWidth() < 50 || image.getHeight() < 50) {
            throw new ValidationException("Image is too small.");
        }

        // 5. Re-optimize to ensure server standards (even if client already did)
        // This ensures all profiles pics are exactly 400px and 80% quality.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Use Thumbnailator for simple, robust resizing
        Thumbnails.of(image)
                .size(maxWidth, maxWidth) // e.g., 400x400
                .keepAspectRatio(true)
                .outputFormat("JPEG")
                .outputQuality(0.7) // Your app's standard quality
                .toOutputStream(baos);

        // 6. Final validation on the server-processed image
        byte[] finalImageBytes = baos.toByteArray();
        if (finalImageBytes.length > (maxFileSizeKB * 1024)) {
            throw new ValidationException("Image is too large.");
        }

        return finalImageBytes; // Now safe to save to the DB
    }

    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

}