package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.AdmissionRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentSkimRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.repository.StudentRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<StudentsResponse> fetchAllStudents() {
        return studentUtil.studentsGlobalList.stream()
                .map(s->studentUtil.mapStudent_ToStudentResponse(s)).collect(Collectors.toList());
    }
    @Override
    public List<StudentsResponse> fetchStudentsByClass(StudentSkimRequest request) {
        return studentUtil.studentsGlobalList.stream()
                //.filter(st->st.getStudentClass().equalsIgnoreCase(request.getStudentClass()))
                .map(s->studentUtil.mapStudent_ToStudentResponse(s)).collect(Collectors.toList());
    }
}
