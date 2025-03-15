package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.AdmissionRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.ParentsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentsImportRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.alien.ApplicationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.ParentsResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Parents;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentUtil {

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
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

    public Students mapBulkStudent_To_Students(StudentsImportRequest s){
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
}
