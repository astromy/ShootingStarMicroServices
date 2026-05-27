package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.StudentAccountRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentSubjectsRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentAccountResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSubjectsResponse;
import com.astromyllc.shootingstar.adminpta.model.StudentAccount;
import com.astromyllc.shootingstar.adminpta.model.StudentSubjects;
import com.astromyllc.shootingstar.adminpta.repository.StudentAccountRepository;
import com.astromyllc.shootingstar.adminpta.repository.StudentSubjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentAccountUtil {

    private final StudentAccountRepository accountRepository;
    public static List<StudentAccount> studentAccountsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StudentAccount mapStudentAccountRequest_ToStudentAccount(StudentAccountRequest r, String studentId) {
        return StudentAccount.builder()
                .studentId(r.getStudentId())
                .activationState(r.getActivationState())
                .activationDate((LocalDateTime.now()).toString())
                .build();
    }

    public void updateStudentAccount(StudentAccount sa, StudentAccountRequest sar, String StudId) {
        sa.setActivationState(sar.getActivationState());
    }

    public void saveAll(List<StudentAccount> sa) {
        accountRepository.saveAll(sa);
        studentAccountsGlobalList.addAll(sa);
    }
    @PostConstruct
    private void fetchAllStudentSubject() {
        studentAccountsGlobalList = accountRepository.findAll();
        log.info("Global Student Accounts List populated with {} records", studentAccountsGlobalList.size());
    }

    public static StudentAccountResponse mapStudentAccount_ToStudentAccountResponse(StudentAccount s) {
        return StudentAccountResponse.builder()
                .studentId(s.getStudentId())
                .activationState(s.getActivationState())
                .activationDate(s.getActivationDate())
                .build();
    }
}
