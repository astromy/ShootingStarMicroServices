package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.AttendanceStatsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.MarkAttendanceRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AttendanceResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.AttendanceStatsResponse;
import com.astromyllc.shootingstar.adminpta.model.Attendance;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.AttendanceRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.AttendanceServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.AttendanceUtil;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService implements AttendanceServiceInterface {

    private static final Set<String> VALID_STATUSES = Set.of("PRESENT", "LATE", "ABSENT");

    private final AttendanceRepository attendanceRepository;

    @Override
    public AttendanceResponse markAttendance(MarkAttendanceRequest request) throws StudentNotEligibleException {
        String status = request.getStatus() == null ? "" : request.getStatus().trim().toUpperCase();
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Status must be PRESENT, LATE or ABSENT.");
        }

        // UTC matches the app, which sends new Date().toISOString().slice(0, 10).
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate date = parseDate(request.getDate(), today);
        if (date.isAfter(today)) {
            throw new IllegalArgumentException("Attendance can't be marked for a future date.");
        }

        Students student = StudentUtil.studentsGlobalList.parallelStream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Attendance rejected: no student found for studentId {}", request.getStudentId());
                    return new StudentNotEligibleException("No student record found for this ID.");
                });

        if (!student.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())) {
            log.warn("Attendance rejected: student {} does not belong to institution {}",
                    request.getStudentId(), request.getInstitutionCode());
            throw new StudentNotEligibleException("This student isn't registered to this school.");
        }

        // Upsert: re-marking the same student on the same day corrects the
        // earlier mark instead of creating a duplicate.
        Attendance record = attendanceRepository
                .findByStudentIdAndDate(student.getStudentId(), date.toString())
                .orElseGet(() -> Attendance.builder()
                        .studentId(student.getStudentId())
                        .institutionCode(student.getInstitutionCode())
                        .date(date.toString())
                        .build());

        record.setStudentClass(student.getStudentClass());
        record.setStatus(status);
        record.setRecordedBy(request.getRecordedBy());
        record.setUpdatedAt(Instant.now());
        attendanceRepository.save(record);

        log.info("Attendance recorded: student {} {} on {} at institution {}",
                record.getStudentId(), status, record.getDate(), record.getInstitutionCode());

        return AttendanceUtil.mapAttendance_ToAttendanceResponse(record);
    }

    @Override
    public AttendanceStatsResponse getAttendanceStats(AttendanceStatsRequest request) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate from = parseDate(request.getDateFrom(), today.withDayOfMonth(1));
        LocalDate to = parseDate(request.getDateTo(), today);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("dateFrom must be on or before dateTo.");
        }

        List<Attendance> records = attendanceRepository.findByInstitutionAndDateRange(
                request.getInstitutionCode(), from.toString(), to.toString());

        long present = records.stream().filter(r -> "PRESENT".equals(r.getStatus())).count();
        long late = records.stream().filter(r -> "LATE".equals(r.getStatus())).count();
        long absent = records.stream().filter(r -> "ABSENT".equals(r.getStatus())).count();
        long total = records.size();
        double rate = total == 0 ? 0.0 : Math.round((present + late) * 1000.0 / total) / 10.0;

        return AttendanceStatsResponse.builder()
                .totalRecords(total)
                .presentCount(present)
                .absentCount(absent)
                .lateCount(late)
                .attendanceRate(rate)
                .dateFrom(from.toString())
                .dateTo(to.toString())
                .build();
    }

    private LocalDate parseDate(String raw, LocalDate fallback) {
        if (raw == null || raw.isBlank()) return fallback;
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Dates must be in YYYY-MM-DD format.");
        }
    }
}
