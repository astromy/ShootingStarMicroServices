package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.request.alien.ApplicationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.*;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.model.StudentSubjects;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import com.astromyllc.shootingstar.adminpta.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentUtil {

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final StudentSubjectUtil studentSubjectUtil;
    private final ParentsUtil parentsUtil;
    
    private final WebClient.Builder webClientBuilder;
    public static List<Students> studentsGlobalList;
    public static List<Parents> parentsGlobalList;
    public static List<StudentsRequest> studentsGlobalRequest;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${gateway.host}")
    private String host;

    @Bean
    private void fetAllStudents() {
        studentsGlobalList = studentRepository.findAll();
        parentsGlobalList=parentRepository.findAll();
        log.info("Global Students List populated with {} records", studentsGlobalList.size());
    }

    private List<ApplicationRequest> fetchStudents(AdmissionRequest admissionRequest) {
        JSONObject json = new JSONObject();
        json.put("institutionCode", admissionRequest.getInstitutionCode());
        json.put("applicationDate", admissionRequest.getApplicationDate());
        json.put("applicationStatus", admissionRequest.getApplicationStatus());
        log.info(json.toJSONString());
        return webClientBuilder.build().post()
                .uri("http://"+host+":8083/api/applications/getProcessedApplicationsBySchool")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(json), JSONObject.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ApplicationRequest>>() {
                }).block();
    }

    public void getCurrentApplications(AdmissionRequest admissionRequest) {
        fetchStudents(admissionRequest).stream().map(this::mapAdmittedStudents).toList();

    }

    private String mapAdmittedStudents(ApplicationRequest s) {
        String studentId = generateStudentId(s.getApplicationInstitution());
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
        parentsList.addAll(s.getParentsRequests().stream().map(sp -> mapStudentParent(sp,studentId)).toList());

        studentRepository.saveAll(studentsList);
        parentRepository.saveAll(parentsList);

        studentsGlobalList.addAll(studentsList);
        parentsGlobalList.addAll(parentsList);
        log.info("{} records have been added to the students list",studentsGlobalList.size());
        return "done";
    }

    private Parents mapStudentParent(ParentsRequest sp,String studentId) {
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

    long studentCount=0l;
    private String generateStudentId(String applicationInstitution) {
       if(studentsGlobalList.size()>0 &&  studentCount<1) {
           studentCount = 1+ studentsGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(applicationInstitution)).count();
       }else{
           studentCount+=1;
       }
       String id=applicationInstitution + StringUtils.right(("00000" + studentCount),5);
       return id;
    }
    public StudentsResponse mapStudent_ToStudentResponse(Students s) {

        List<ParentsResponse> p = Optional.ofNullable(s.getParentsList())
                .orElse(Collections.emptyList())
                .stream()
                .filter(d -> d.getInstitutionCode().equalsIgnoreCase(s.getInstitutionCode()))
                .map(ParentsUtil::mapParents_ToParentsResponse)
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
                .studentClass(s.getStudentClass())
                .studentParents(parentsGlobalList.stream().filter(sp->sp.getStudentId().equalsIgnoreCase(s.getStudentId())).map(this::mapParent_ToParentResponse).toList())
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

    public ParentsResponse mapParent_ToParentResponse(Parents p){
        return ParentsResponse.builder()
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

    public Students mapStudentsRequest_To_Students(StudentsImportRequest s){
        String studentId = s.getStudentId().trim().isEmpty() ? generateStudentId(s.getInstitutionCode()) : s.getStudentId();
        if (s.getFirstName() == null) {
            generateStudentId(s.getInstitutionCode());
        }

        List<Parents> parentsList=new ArrayList<>();

        if(s.getFirstName()==null) {
            generateStudentId(s.getInstitutionCode());
        }
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
                .build();
    }

    public Students mapStudentsRequest_To_Students(StudentsImportRequest s, Students es){
        String studentId = s.getStudentId().trim().isEmpty() ? generateStudentId(s.getInstitutionCode()) : s.getStudentId();
        if (s.getFirstName() == null) {
            generateStudentId(s.getInstitutionCode());
        }

        List<Parents> parentsList=new ArrayList<>();

        /*if(s.getStudentId().trim().isEmpty()) {
            generateStudentId(s.getInstitutionCode());
        }*/

        if(s.getFirstName()==null) {
            generateStudentId(s.getInstitutionCode());
        }

        parentsList.addAll(
                s.getParentsRequests().stream()
                        .map(parentRequest -> mapStudentParent(parentRequest, studentId))
                        .toList()
        );

        /*parentsList.add(mapStudentParent(s.getParentsRequests().get(0),studentId));
        parentsList.add(mapStudentParent(s.getParentsRequests().get(1),studentId));*/
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
                .build();
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



    public Optional<StudentsResponse> updateExistingStudents(StudentsImportRequest studentsImportRequest, Students existingStudent) throws URISyntaxException, IOException {
        Students updatedStudents = mapStudentsRequest_To_Students(studentsImportRequest, existingStudent);

        this.<ParentsRequest, Parents>updateRecords(
                studentsImportRequest.getParentsRequests(),
                existingStudent.getParentsList(),
                ParentsUtil::mapParentRequest_ToParent,
                parentsUtil::updateParents,
                parentsUtil::saveAll,
                updatedStudents::setParentsList,
                updatedStudents.getStudentId(),
                (ent, req) -> ent.getParentType().equalsIgnoreCase(req.getParentType()) &&
                        ent.getStudentId().equals(req.getStudentId())
        );

        this.<StudentSubjectsRequest, StudentSubjects>updateRecords(
                studentsImportRequest.getStudentSubjectsRequests(),
                existingStudent.getStudentSubjects(),
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
            List<ENT> updated = requestRecords.stream()
                    .map(req -> {
                        Optional<ENT> existing = existingRecords.stream()
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

    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
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


}
