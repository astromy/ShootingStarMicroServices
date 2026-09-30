package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.StudentAccountRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentAccountResponse;
import com.astromyllc.shootingstar.adminpta.model.StudentAccount;
import com.astromyllc.shootingstar.adminpta.repository.StudentAccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentAccountUtil {

    // normalized studentId -> all account rows for that student (history; latest row = current state)
    private static final Map<String, List<StudentAccount>> BY_STUDENT = new ConcurrentHashMap<>();
    private final StudentAccountRepository accountRepository;

    // The old code matched studentIds with equalsIgnoreCase, so keys are normalized to keep that behaviour
    private static String key(String studentId) {
        return studentId.trim().toUpperCase();
    }

    public static List<StudentAccount> getAll(String studentId) {
        if (studentId == null) return Collections.emptyList();
        return BY_STUDENT.getOrDefault(key(studentId), Collections.emptyList());
    }

    /**
     * Latest row by activationDate — this is the student's current state.
     */
    public static StudentAccount getLatest(String studentId) {
        return latestOf(getAll(studentId));
    }

    /**
     * Active only if the LATEST row is active, so a deactivated student can reactivate.
     */
    public static boolean isActive(String studentId) {
        StudentAccount latest = getLatest(studentId);
        return latest != null && "active".equalsIgnoreCase(latest.getActivationState());
    }

    public static List<StudentAccountResponse> getAllResponses(String studentId) {
        return getAll(studentId).stream()
                .map(StudentAccountUtil::mapStudentAccount_ToStudentAccountResponse)
                .toList();
    }

    private static StudentAccount latestOf(List<StudentAccount> list) {
        if (list == null || list.isEmpty()) return null;
        StudentAccount best = null;
        for (StudentAccount sa : list) {
            String d = sa.getActivationDate();
            if (d != null && (best == null || d.compareTo(best.getActivationDate()) > 0)) best = sa;
        }
        return best != null ? best : list.get(list.size() - 1);
    }

    /**
     * Adds to the cache only if the row isn't already there (updated rows are already cached).
     */
    private static void mirror(StudentAccount s) {
        List<StudentAccount> list = BY_STUDENT.computeIfAbsent(key(s.getStudentId()), k -> new CopyOnWriteArrayList<>());
        boolean present = list.stream().anyMatch(x -> x == s
                || (x.getId() != null && x.getId().equals(s.getId())));
        if (!present) list.add(s);
    }

    public static StudentAccount mapStudentAccountRequest_ToStudentAccount(StudentAccountRequest r, String studentId) {
        return StudentAccount.builder()
                .studentId(r.getStudentId())
                .activationState(r.getActivationState())
                .activationDate(LocalDateTime.now().toString())
                .build();
    }

    public static StudentAccountResponse mapStudentAccount_ToStudentAccountResponse(StudentAccount s) {
        return StudentAccountResponse.builder()
                .studentId(s.getStudentId())
                .activationState(s.getActivationState())
                .activationDate(s.getActivationDate())
                .build();
    }

    /**
     * Webhook path: appends an "active" row unless the student is already active.
     * compute() is atomic per key, so concurrent Paystack retries can't both append.
     * Returns true if this call activated the student.
     */
    public boolean activateIfNotActive(String studentId) {
        StudentAccount account = StudentAccount.builder()
                .studentId(studentId)
                .activationState("active")
                .activationDate(LocalDateTime.now().toString())
                .build();

        boolean[] appended = {false};
        BY_STUDENT.compute(key(studentId), (k, list) -> {
            List<StudentAccount> current = list == null ? new CopyOnWriteArrayList<>() : list;
            StudentAccount latest = latestOf(current);
            if (latest == null || !"active".equalsIgnoreCase(latest.getActivationState())) {
                current.add(account);
                appended[0] = true;
            }
            return current;
        });

        if (appended[0]) {
            try {
                accountRepository.save(account);
            } catch (RuntimeException e) {
                getAll(studentId).remove(account);   // keep cache consistent with DB
                throw e;
            }
        }
        return appended[0];
    }

    public StudentAccount persist(StudentAccount sa) {
        StudentAccount saved = accountRepository.save(sa);
        mirror(saved);
        return saved;
    }

    public void saveAll(List<StudentAccount> sa) {
        accountRepository.saveAll(sa).forEach(StudentAccountUtil::mirror);
    }

    @PostConstruct
    private void load() {
        BY_STUDENT.clear();
        accountRepository.findAll().forEach(StudentAccountUtil::mirror);
        log.info("Student account cache loaded: {} students, {} rows",
                BY_STUDENT.size(),
                BY_STUDENT.values().stream().mapToInt(List::size).sum());
    }

    public void updateStudentAccount(StudentAccount sa, StudentAccountRequest sar, String studId) {
        sa.setActivationState(sar.getActivationState());
        sa.setActivationDate(LocalDateTime.now().toString());
        accountRepository.save(sa);
    }
}