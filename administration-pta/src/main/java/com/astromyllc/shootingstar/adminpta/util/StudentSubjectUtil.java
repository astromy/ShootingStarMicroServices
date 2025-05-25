package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.StudentSubjectsRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSubjectsResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.model.StudentSubjects;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import com.astromyllc.shootingstar.adminpta.repository.StudentSubjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentSubjectUtil {

    private final StudentSubjectRepository studentSubjectRepository;
    public static List<StudentSubjects> studentSubjectGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StudentSubjects mapStudentsSubjectRequest_ToStudentsSubjects(StudentSubjectsRequest r, String studentId) {
        return StudentSubjects.builder()
                .subjectName(r.getSubjectName())
                .build();
    }

    public void updateStudentSubjects(StudentSubjects s, StudentSubjectsRequest ss, String StudId) {
        s.setSubjectName(ss.getSubjectName());
    }

    public void saveAll(List<StudentSubjects> ss) {
        studentSubjectRepository.saveAll(ss);
        studentSubjectGlobalList.addAll(ss);
    }
    @PostConstruct
    private void fetchAllStudentSubject() {
        studentSubjectGlobalList = studentSubjectRepository.findAll();
        log.info("Global Student Subjects List populated with {} records", studentSubjectGlobalList.size());
    }

    public static StudentSubjectsResponse mapStudentSubject_ToStudentSubjectResponse(StudentSubjects s) {
        return StudentSubjectsResponse.builder()
                .subjectName(s.getSubjectName())
                .build();
    }
}
