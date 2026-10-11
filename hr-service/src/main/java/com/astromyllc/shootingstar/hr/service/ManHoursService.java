package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.calc.OvertimeClassifier;
import com.astromyllc.shootingstar.hr.calc.WorkCalendar;
import com.astromyllc.shootingstar.hr.dto.request.ManHoursRequest;
import com.astromyllc.shootingstar.hr.dto.response.ManHoursRow;
import com.astromyllc.shootingstar.hr.model.ClockEvent;
import com.astromyllc.shootingstar.hr.model.LeaveRequest;
import com.astromyllc.shootingstar.hr.model.OvertimeRecord;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.model.WorkHoursSettings;
import com.astromyllc.shootingstar.hr.repository.ClockEventRepository;
import com.astromyllc.shootingstar.hr.repository.OvertimeRecordRepository;
import com.astromyllc.shootingstar.hr.utils.StaffDirectory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Hours a person was expected against hours worked, for a month, with public
 * holidays, rest days and approved leave taken out of what was expected.
 */
@Service
public class ManHoursService {

    private final ClockEventRepository clockEvents;
    private final OvertimeRecordRepository overtimeRecords;
    private final CalendarService calendars;
    private final LeaveService leaves;

    public ManHoursService(ClockEventRepository clockEvents, OvertimeRecordRepository overtimeRecords,
                           CalendarService calendars, LeaveService leaves) {
        this.clockEvents = clockEvents;
        this.overtimeRecords = overtimeRecords;
        this.calendars = calendars;
        this.leaves = leaves;
    }

    public List<ManHoursRow> monthly(ManHoursRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        LocalDate today = LocalDate.now(CalendarService.ZONE);
        YearMonth ym = YearMonth.of(r.year() != null ? r.year() : today.getYear(),
                (r.month() != null ? r.month() : today.getMonthValue() - 1) + 1);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        WorkHoursSettings settings = calendars.settings(code);
        WorkCalendar calendar = calendars.calendar(code, settings);
        double dailyHours = CalendarService.paidHoursPerDay(settings);

        int workingDays = calendar.workingDaysBetween(start, end);
        int holidayDays = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (calendar.isHoliday(d) && calendar.isWorkDayOfWeek(d)) holidayDays++;
        }

        Map<String, List<ClockEvent>> eventsByStaff = new HashMap<>();
        for (ClockEvent e : clockEvents.findByInstitutionCodeAndTimestampBetween(code,
                start.minusDays(1).atStartOfDay(CalendarService.ZONE).toInstant(),
                end.plusDays(2).atStartOfDay(CalendarService.ZONE).toInstant())) {
            if (e.getStaffCode() != null) {
                eventsByStaff.computeIfAbsent(e.getStaffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(e);
            }
        }
        Map<String, List<LeaveRequest>> leaveByStaff = new HashMap<>();
        for (LeaveRequest l : leaves.approvedForInstitution(code)) {
            leaveByStaff.computeIfAbsent(l.getStaffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(l);
        }
        Map<String, List<OvertimeRecord>> overtimeByStaff = new HashMap<>();
        for (OvertimeRecord o : overtimeRecords.findByInstitutionCode(code)) {
            LocalDate d = LocalDate.parse(o.getDate());
            if (d.isBefore(start) || d.isAfter(end)) continue;
            overtimeByStaff.computeIfAbsent(o.getStaffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(o);
        }

        List<ManHoursRow> rows = new ArrayList<>();
        for (Staff staff : StaffDirectory.inInstitution(code)) {
            if (r.staffCode() != null && !r.staffCode().isBlank() && !r.staffCode().equalsIgnoreCase(staff.getStaffCode())) continue;
            String key = staff.getStaffCode() == null ? "" : staff.getStaffCode().toLowerCase(Locale.ROOT);

            Set<LocalDate> leaveWorkingDays = new HashSet<>();
            for (LeaveRequest l : leaveByStaff.getOrDefault(key, List.of())) {
                LocalDate from = LocalDate.parse(l.getStartDate()).isBefore(start) ? start : LocalDate.parse(l.getStartDate());
                LocalDate to = LocalDate.parse(l.getEndDate()).isAfter(end) ? end : LocalDate.parse(l.getEndDate());
                for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                    if (calendar.isWorkingDay(d)) leaveWorkingDays.add(d);
                }
            }
            int leaveDays = leaveWorkingDays.size();
            int expectedDays = Math.max(0, workingDays - leaveDays);

            List<OvertimeClassifier.ClockPoint> points = new ArrayList<>();
            for (ClockEvent e : eventsByStaff.getOrDefault(key, List.of())) {
                if (e.getTimestamp() == null || e.getType() == null) continue;
                boolean in = "IN".equalsIgnoreCase(e.getType());
                if (in || "OUT".equalsIgnoreCase(e.getType())) points.add(new OvertimeClassifier.ClockPoint(e.getTimestamp(), in));
            }
            Set<LocalDate> present = new HashSet<>();
            double workedHours = 0;
            for (OvertimeClassifier.Session s : OvertimeClassifier.pair(points, OvertimeService.MAX_SESSION_HOURS).sessions()) {
                LocalDate d = s.in().atZone(CalendarService.ZONE).toLocalDate();
                if (d.isBefore(start) || d.isAfter(end)) continue;
                present.add(d);
                workedHours += Duration.between(s.in(), s.out()).toMinutes() / 60.0;
            }

            double approved = 0;
            double pending = 0;
            for (OvertimeRecord o : overtimeByStaff.getOrDefault(key, List.of())) {
                if (OvertimeService.APPROVED.equals(o.getStatus())) {
                    approved += (o.getApprovedMinutes() != null ? o.getApprovedMinutes() : o.getMinutes()) / 60.0;
                } else if (OvertimeService.PENDING.equals(o.getStatus())) {
                    pending += o.getMinutes() / 60.0;
                }
            }

            rows.add(new ManHoursRow(staff.getStaffCode(), StaffDirectory.fullName(staff), workingDays, holidayDays,
                    leaveDays, expectedDays, round2(expectedDays * dailyHours), present.size(), round2(workedHours),
                    round2(approved), round2(pending)));
        }
        rows.sort(Comparator.comparing(ManHoursRow::staffName));
        return rows;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
