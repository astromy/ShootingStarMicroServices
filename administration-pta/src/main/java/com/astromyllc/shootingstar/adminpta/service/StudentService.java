package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimWithParentResponse;
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
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
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
    public Optional<List<StudentSkimWithParentResponse>> getSkimpStudentsByParentContact(SingleStringRequest request) {
        List<Students> matches = StudentUtil.findStudentsByContact(request.getVal());
        return Optional.of(
                matches.stream()
                        .map(studentUtil::mapStudent_ToStudentSkimWithParentResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<List<StudentsResponse>> postBulkStudentList(List<StudentsImportRequest> request) {

        Optional<List<StudentsResponse>> studentsResponse = request.stream()
                .map(studentRequest -> {
                    Optional<Students> existingStudent = StudentUtil.studentsGlobalList.stream()
                            .filter(s -> s.getInstitutionCode().equalsIgnoreCase(studentRequest.getInstitutionCode()) &&
                                    s.getFirstName().equalsIgnoreCase(studentRequest.getFirstName()) &&
                                    s.getLastName().equalsIgnoreCase(studentRequest.getLastName()) &&
                                    s.getDateOfBirth().equals(studentRequest.getDateOfBirth())
                            )
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

    @Override
    public Optional<List<StudentsResponse>> fetchStudentsByDynamicData(DynamicStringRequest institution) {
        if (institution == null || institution.getKey() == null || institution.getVal() == null) {
            return Optional.empty();
        }

        // Pre-process field types
        Map<String, Function<Students, Object>> fieldGetters = Map.of(
                "institutionCode", Students::getInstitutionCode,
                "studentClass", Students::getStudentClass,
                "dateOfBirth", Students::getDateOfBirth,
                "status", Students::getStatus
                // Add all other fields here
        );

        // Type handlers
        Map<Class<?>, BiPredicate<Object, Object>> typeHandlers = Map.of(
                String.class, (expected, actual) ->
                        actual != null && ((String) actual).equalsIgnoreCase((String) expected),
                LocalDate.class, Objects::equals,
                Boolean.class, Objects::equals
                // Add other types as needed
        );

        List<StudentsResponse> result = StudentUtil.studentsGlobalList.stream()
                .filter(student -> {
                    for (int i = 0; i < institution.getKey().size(); i++) {
                        String key = institution.getKey().get(i);
                        String stringValue = institution.getVal().get(i);

                        // Skip if value is null or empty
                        if (stringValue == null || stringValue.trim().isEmpty()) {
                            continue;
                        }

                        if (!fieldGetters.containsKey(key)) {
                            return false;
                        }

                        try {
                            Object actualValue = fieldGetters.get(key).apply(student);
                            Class<?> fieldType = actualValue != null ? actualValue.getClass() : String.class;

                            Object expectedValue = convertValue(stringValue, fieldType);
                            if (!typeHandlers.getOrDefault(fieldType, Objects::equals)
                                    .test(expectedValue, actualValue)) {
                                return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(studentUtil::mapStudent_ToStudentResponse)
                .toList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    @Override
    public Optional<StudentSkimResponse> getStudentByID(SingleStringRequest request) {
        return Optional.ofNullable(request.getVal())
                .flatMap(val -> StudentUtil.studentsGlobalList.parallelStream()
                        .filter(x -> x.getStudentId().equalsIgnoreCase(val))
                        .findFirst()
                        .map(studentUtil::mapStudent_ToSkimpStudentResponse)
                );
    }

    private Object convertValue(String stringValue, Class<?> targetType) {
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return null;
        }
        if (targetType == String.class) return stringValue;
        if (targetType == LocalDate.class) return LocalDate.parse(stringValue);
        if (targetType == Boolean.class) return Boolean.parseBoolean(stringValue);
        return stringValue;
    }
}
