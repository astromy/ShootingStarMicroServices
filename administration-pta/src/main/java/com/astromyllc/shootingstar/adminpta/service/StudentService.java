package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.StudentRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentService implements StudentServiceInterface {
    private final StudentRepository studentRepository;
    private final StudentUtil studentUtil;


    @Override
    public void fetchCurrentApplications(AdmissionRequest admissionRequest) {
        studentUtil.getCurrentApplications(admissionRequest);
    }

    @Override
    public Optional<List<StudentsResponse>> fetchAllStudents() {
        return Optional.of(StudentUtil.studentsGlobalList.stream()
                .map(studentUtil::mapStudent_ToStudentResponse).toList());
    }
    @Override
    public Optional<List<StudentsResponse>> fetchStudentsByClass(ClassListRequest request) {
        return Optional.of(StudentUtil.studentsGlobalList.stream()
                .filter(st->st.getStudentClass().equalsIgnoreCase(request.getStudentClass()))
                .map(studentUtil::mapStudent_ToStudentResponse).toList());
    }
    @Override
    public Optional<List<StudentSkimResponse>> fetchSkimpStudentsByClass(ClassListRequest request) {
        return Optional.of(StudentUtil.studentsGlobalList.stream()
                .filter(st->st.getStudentClass().equalsIgnoreCase(request.getStudentClass()))
                .map(studentUtil::mapStudent_ToSkimpStudentResponse).toList());
    }

    @Override
    public Optional<List<StudentsResponse>> postBulkStudentList(List<StudentsImportRequest> request) {
     /* List<Students> studentsList=  request.stream().map(studentUtil::mapStudentsRequest_To_Students).toList();
        studentRepository.saveAll(studentsList);
        StudentUtil.studentsGlobalList.addAll(studentsList);
        log.info("{} New records have been persited into the Database",studentsList.size());*/
       // return Optional.of(studentsList.stream().map(studentUtil::mapStudent_ToStudentResponse).toList());

        Optional<List<StudentsResponse>> studentsResponse = request.stream()
                .map(studentRequest -> {
                    Optional<Students> existingStudent = StudentUtil.studentsGlobalList.stream()
                            .filter(s -> s.getInstitutionCode().equalsIgnoreCase(studentRequest.getInstitutionCode()) &&
                                    s.getStudentId().equalsIgnoreCase(studentRequest.getStudentId()))
                            .findFirst();

                    return existingStudent
                            .map(student -> {
                                try {
                                    return studentUtil.updateExistingStudents(studentRequest, student);
                                } catch (URISyntaxException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .orElseGet(() -> {
                                try {
                                    return studentUtil.createNewStudents(studentRequest);
                                } catch (URISyntaxException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                })
                .filter(Optional::isPresent) // Keep only present Optionals
                .map(Optional::get)          // Unwrap them
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.isEmpty() ? Optional.empty() : Optional.of(list)
                ));
        return studentsResponse;
    }

    @Override
    public Optional<List<ClassListResponse>> fetchAssessmentList(ClassListRequest request) {
        String key = request.getInstitutionCode().toLowerCase() + ":" + request.getStudentClass().toLowerCase();
        List<Students> students = StudentUtil.indexedStudents.getOrDefault(key, Collections.emptyList());

        if (students.isEmpty()) return Optional.empty();

        List<ClassListResponse> responses = students.stream()
                .map(studentUtil::mapStudent_ToClassListResponse)
                .toList();

        return Optional.of(responses);
    }

    /*public Optional<List<ClassListResponse>> fetchAssessmentList(ClassListRequest request) {
        return Optional.of(StudentUtil.studentsGlobalList.stream()
                .filter(x->
                        x.getStudentClass().equalsIgnoreCase(request.getStudentClass()) &&
                        x.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode()))
                .map(studentUtil::mapStudent_ToClassListResponse).toList());
    }*/



    @Override
    public Optional<List<StudentsResponse>> fetchStudentsByStatus(SingleStringRequest status) {
        return Optional.of(StudentUtil.studentsGlobalList.stream().filter(x->x.getStatus().equalsIgnoreCase(status.getVal()))
                .map(studentUtil::mapStudent_ToStudentResponse).toList()
        );
    }

    @Override
    public Optional<List<StudentsResponse>> fetchStudentsByInstitution(SingleStringRequest institution) {
        return Optional.of(StudentUtil.studentsGlobalList.stream().filter(x->x.getInstitutionCode().equalsIgnoreCase(institution.getVal()))
                .map(studentUtil::mapStudent_ToStudentResponse).toList()
        );
    }
}
