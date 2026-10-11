package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.calc.OvertimeClassifier;
import com.astromyllc.shootingstar.hr.calc.WorkCalendar;
import com.astromyllc.shootingstar.hr.dto.request.AttendanceQueryRequest;
import com.astromyllc.shootingstar.hr.dto.request.ClockWebRequest;
import com.astromyllc.shootingstar.hr.dto.response.AttendanceRow;
import com.astromyllc.shootingstar.hr.exception.HrValidationException;
import com.astromyllc.shootingstar.hr.model.ClockEvent;
import com.astromyllc.shootingstar.hr.model.LeaveRequest;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.repository.ClockEventRepository;
import com.astromyllc.shootingstar.hr.utils.StaffDirectory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Attendance from clock events - the same events Pulse records, so a web
 * check-in and a gate clock-in are one record of the day. Holidays, rest days
 * and approved leave are applied here, so a staff member isn't shown absent
 * on a day they were never expected.
 */
@Service
public class AttendanceService {

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    private final ClockEventRepository clockEvents;
    private final CalendarService calendars;
    private final LeaveService leaves;
    private final OvertimeService overtime;

    public AttendanceService(ClockEventRepository clockEvents, CalendarService calendars, LeaveService leaves,
                             OvertimeService overtime) {
        this.clockEvents = clockEvents;
        this.calendars = calendars;
        this.leaves = leaves;
        this.overtime = overtime;
    }

    public AttendanceRow checkIn(ClockWebRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        Staff staff = LeaveService.resolveStaff(code, r.employeeId(), r.actorStaffCode());
        Instant now = Instant.now();
        ClockEvent last = latest(staff.getStaffCode(), now);
        if (last != null && "IN".equalsIgnoreCase(last.getType())) {
            throw new HrValidationException("You're already clocked in (since "
                    + HHMM.format(last.getTimestamp().atZone(CalendarService.ZONE)) + ").");
        }
        record(staff, code, "IN", now);
        return todayRow(code, staff);
    }

    public AttendanceRow checkOut(ClockWebRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        Staff staff = LeaveService.resolveStaff(code, r.employeeId(), r.actorStaffCode());
        Instant now = Instant.now();
        ClockEvent last = latest(staff.getStaffCode(), now);
        if (last == null || !"IN".equalsIgnoreCase(last.getType())) {
            throw new HrValidationException("You're not clocked in.");
        }
        record(staff, code, "OUT", now);
        overtime.detectForStaffDay(code, staff.getStaffCode(), LocalDate.now(CalendarService.ZONE));
        return todayRow(code, staff);
    }

    public List<AttendanceRow> attendance(AttendanceQueryRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        LocalDate today = LocalDate.now(CalendarService.ZONE);
        WorkCalendar calendar = calendars.calendar(code);
        boolean everyone = r.employeeId() == null || r.employeeId().isBlank();

        if (everyone) {
            LocalDate day = r.date() != null ? r.date() : today;
            return dayForEveryone(code, day, today, calendar);
        }

        Staff staff = LeaveService.resolveStaff(code, r.employeeId(), r.actorStaffCode());
        List<LocalDate> days = new ArrayList<>();
        if (r.date() != null) {
            days.add(r.date());
        } else {
            YearMonth ym = YearMonth.of(r.year() != null ? r.year() : today.getYear(),
                    (r.month() != null ? r.month() : today.getMonthValue() - 1) + 1);
            for (LocalDate d = ym.atDay(1); !d.isAfter(ym.atEndOfMonth()); d = d.plusDays(1)) days.add(d);
        }
        LocalDate first = days.get(0);
        LocalDate last = days.get(days.size() - 1);
        List<ClockEvent> events = clockEvents.findByStaffCodeAndTimestampBetween(staff.getStaffCode(),
                first.minusDays(1).atStartOfDay(CalendarService.ZONE).toInstant(),
                last.plusDays(2).atStartOfDay(CalendarService.ZONE).toInstant());
        return rowsFor(staff, events, days, calendar, leaves.approvedFor(code, staff.getStaffCode()), today);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private List<AttendanceRow> dayForEveryone(String code, LocalDate day, LocalDate today, WorkCalendar calendar) {
        List<ClockEvent> events = clockEvents.findByInstitutionCodeAndTimestampBetween(code,
                day.minusDays(1).atStartOfDay(CalendarService.ZONE).toInstant(),
                day.plusDays(2).atStartOfDay(CalendarService.ZONE).toInstant());
        Map<String, List<ClockEvent>> eventsByStaff = new HashMap<>();
        for (ClockEvent e : events) {
            if (e.getStaffCode() != null) {
                eventsByStaff.computeIfAbsent(e.getStaffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(e);
            }
        }
        Map<String, List<LeaveRequest>> leaveByStaff = new HashMap<>();
        for (LeaveRequest l : leaves.approvedForInstitution(code)) {
            leaveByStaff.computeIfAbsent(l.getStaffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(l);
        }

        List<AttendanceRow> out = new ArrayList<>();
        for (Staff staff : StaffDirectory.inInstitution(code)) {
            String key = staff.getStaffCode() == null ? "" : staff.getStaffCode().toLowerCase(Locale.ROOT);
            out.addAll(rowsFor(staff, eventsByStaff.getOrDefault(key, List.of()), List.of(day), calendar,
                    leaveByStaff.getOrDefault(key, List.of()), today));
        }
        out.sort(Comparator.comparing(AttendanceRow::employeeName, Comparator.nullsLast(Comparator.naturalOrder())));
        return out;
    }

    private List<AttendanceRow> rowsFor(Staff staff, List<ClockEvent> events, List<LocalDate> days,
                                        WorkCalendar calendar, List<LeaveRequest> approvedLeave, LocalDate today) {
        Map<LocalDate, Instant> firstIn = new HashMap<>();
        Map<LocalDate, Instant> lastOut = new HashMap<>();
        List<OvertimeClassifier.ClockPoint> points = new ArrayList<>();
        for (ClockEvent e : events) {
            if (e.getTimestamp() == null || e.getType() == null) continue;
            boolean in = "IN".equalsIgnoreCase(e.getType());
            if (!in && !"OUT".equalsIgnoreCase(e.getType())) continue;
            points.add(new OvertimeClassifier.ClockPoint(e.getTimestamp(), in));
            LocalDate d = e.getTimestamp().atZone(CalendarService.ZONE).toLocalDate();
            if (in) {
                firstIn.merge(d, e.getTimestamp(), (a, b) -> a.isBefore(b) ? a : b);
            } else {
                lastOut.merge(d, e.getTimestamp(), (a, b) -> a.isAfter(b) ? a : b);
            }
        }

        // Hours come from paired sessions, credited to the day the session started.
        Map<LocalDate, Double> hours = new HashMap<>();
        for (OvertimeClassifier.Session s : OvertimeClassifier.pair(points, OvertimeService.MAX_SESSION_HOURS).sessions()) {
            LocalDate d = s.in().atZone(CalendarService.ZONE).toLocalDate();
            hours.merge(d, Duration.between(s.in(), s.out()).toMinutes() / 60.0, Double::sum);
        }

        String name = StaffDirectory.fullName(staff);
        List<AttendanceRow> rows = new ArrayList<>();
        for (LocalDate d : days) {
            Instant in = firstIn.get(d);
            Instant out = lastOut.get(d);
            if (in != null && out != null && out.isBefore(in)) out = null; // that OUT closes an earlier day's session

            // Holidays and rest days come before leave: they were never working days, so they
            // don't use up leave and shouldn't be reported as "On Leave".
            String status;
            if (in != null) {
                status = "Present";
            } else if (calendar.isHoliday(d)) {
                status = "Holiday";
            } else if (!calendar.isWorkDayOfWeek(d)) {
                status = "Rest Day";
            } else if (onLeave(approvedLeave, d)) {
                status = "On Leave";
            } else if (d.isAfter(today)) {
                continue; // hasn't happened yet
            } else if (d.equals(today)) {
                status = "Not in yet";
            } else {
                status = "Absent";
            }
            double h = Math.round(hours.getOrDefault(d, 0.0) * 100.0) / 100.0;
            rows.add(new AttendanceRow(staff.getStaffCode(), name, d, status, in, out, h));
        }
        return rows;
    }

    private static boolean onLeave(List<LeaveRequest> approved, LocalDate d) {
        for (LeaveRequest l : approved) {
            if (!d.isBefore(LocalDate.parse(l.getStartDate())) && !d.isAfter(LocalDate.parse(l.getEndDate()))) return true;
        }
        return false;
    }

    private AttendanceRow todayRow(String code, Staff staff) {
        LocalDate today = LocalDate.now(CalendarService.ZONE);
        List<ClockEvent> events = clockEvents.findByStaffCodeAndTimestampBetween(staff.getStaffCode(),
                today.minusDays(1).atStartOfDay(CalendarService.ZONE).toInstant(),
                today.plusDays(2).atStartOfDay(CalendarService.ZONE).toInstant());
        List<AttendanceRow> rows = rowsFor(staff, events, List.of(today), calendars.calendar(code),
                leaves.approvedFor(code, staff.getStaffCode()), today);
        return rows.get(0);
    }

    /** The staff member's most recent event in the last 17 hours, or null. */
    private ClockEvent latest(String staffCode, Instant now) {
        List<ClockEvent> recent = clockEvents.findByStaffCodeAndTimestampBetween(staffCode,
                now.minus(Duration.ofHours(17)), now.plus(Duration.ofMinutes(1)));
        ClockEvent best = null;
        for (ClockEvent e : recent) {
            if (e.getTimestamp() == null) continue;
            if (best == null || e.getTimestamp().isAfter(best.getTimestamp())) best = e;
        }
        return best;
    }

    private void record(Staff staff, String code, String type, Instant at) {
        ClockEvent event = ClockEvent.builder()
                .staffCode(staff.getStaffCode())
                .institutionCode(code)
                .type(type)
                .latitude(0)
                .longitude(0)
                .timestamp(at)
                .source("WEB")
                .build();
        clockEvents.save(event);
    }
}
