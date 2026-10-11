package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.WorkHoursSettingsRequest;
import com.astromyllc.shootingstar.hr.dto.response.WorkHoursSettingsResponse;
import com.astromyllc.shootingstar.hr.exception.HrValidationException;
import com.astromyllc.shootingstar.hr.model.WorkHoursSettings;
import com.astromyllc.shootingstar.hr.repository.WorkHoursSettingsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class WorkHoursService {

    private static final Set<Integer> ROUNDING = Set.of(1, 5, 10, 15, 30, 60);

    private final WorkHoursSettingsRepository repository;
    private final CalendarService calendars;

    public WorkHoursService(WorkHoursSettingsRepository repository, CalendarService calendars) {
        this.repository = repository;
        this.calendars = calendars;
    }

    public WorkHoursSettingsResponse get(String institutionCode) {
        String code = HolidayService.requireInstitution(institutionCode);
        return toResponse(calendars.settings(code), calendars.isConfigured(code));
    }

    public WorkHoursSettingsResponse save(WorkHoursSettingsRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        WorkHoursSettings s = repository.findByInstitutionCode(code).orElseGet(() -> CalendarService.defaults(code));

        if (r.workDays() != null) {
            Set<Integer> days = new TreeSet<>();
            for (Integer d : r.workDays()) {
                if (d == null || d < 1 || d > 7) throw new HrValidationException("Working days must be Monday to Sunday.");
                days.add(d);
            }
            if (days.isEmpty()) throw new HrValidationException("Choose at least one working day.");
            s.setWorkDays(new ArrayList<>(days));
        }
        if (r.startTime() != null) s.setStartTime(time(r.startTime(), "opening time"));
        if (r.endTime() != null) s.setEndTime(time(r.endTime(), "closing time"));
        if (!LocalTime.parse(s.getStartTime()).isBefore(LocalTime.parse(s.getEndTime()))) {
            throw new HrValidationException("Closing time must be after opening time.");
        }
        if (r.breakMinutes() != null) {
            if (r.breakMinutes() < 0 || r.breakMinutes() > 240) {
                throw new HrValidationException("The unpaid break must be between 0 and 240 minutes.");
            }
            s.setBreakMinutes(r.breakMinutes());
        }
        if (CalendarService.paidHoursPerDay(s) <= 0) {
            throw new HrValidationException("The working day is shorter than the unpaid break.");
        }
        if (r.minOvertimeMinutes() != null) {
            if (r.minOvertimeMinutes() < 0 || r.minOvertimeMinutes() > 480) {
                throw new HrValidationException("The minimum overtime must be between 0 and 480 minutes.");
            }
            s.setMinOvertimeMinutes(r.minOvertimeMinutes());
        }
        if (r.roundToMinutes() != null) {
            if (!ROUNDING.contains(r.roundToMinutes())) {
                throw new HrValidationException("Round overtime down to 1, 5, 10, 15, 30 or 60 minutes.");
            }
            s.setRoundToMinutes(r.roundToMinutes());
        }
        if (r.countBeforeStart() != null) s.setCountBeforeStart(r.countBeforeStart());
        if (r.multiplierAfterHours() != null) s.setMultiplierAfterHours(multiplier(r.multiplierAfterHours(), "after-hours"));
        if (r.multiplierWeekend() != null) s.setMultiplierWeekend(multiplier(r.multiplierWeekend(), "weekend"));
        if (r.multiplierHoliday() != null) s.setMultiplierHoliday(multiplier(r.multiplierHoliday(), "public-holiday"));
        if (r.monthlyHoursDivisor() != null) {
            if (r.monthlyHoursDivisor() < 100 || r.monthlyHoursDivisor() > 300) {
                throw new HrValidationException("Hours per month (used for the hourly rate) should be between 100 and 300.");
            }
            s.setMonthlyHoursDivisor(r.monthlyHoursDivisor());
        }
        if (r.overtimeEnabled() != null) s.setOvertimeEnabled(r.overtimeEnabled());

        s.setUpdatedBy(r.updatedBy());
        s.setUpdatedAt(Instant.now());
        return toResponse(repository.save(s), true);
    }

    private static String time(String value, String label) {
        try {
            return LocalTime.parse(value.trim()).toString();
        } catch (DateTimeParseException e) {
            throw new HrValidationException("Enter the " + label + " as hours and minutes, e.g. 08:00.");
        }
    }

    private static double multiplier(double value, String label) {
        if (value < 1 || value > 5) {
            throw new HrValidationException("The " + label + " rate must be between 1 and 5 times normal pay.");
        }
        return value;
    }

    private static WorkHoursSettingsResponse toResponse(WorkHoursSettings s, boolean configured) {
        List<Integer> days = s.getWorkDays() == null ? List.of() : s.getWorkDays();
        return new WorkHoursSettingsResponse(configured, days, s.getStartTime(), s.getEndTime(), s.getBreakMinutes(),
                s.getMinOvertimeMinutes(), s.getRoundToMinutes(), s.isCountBeforeStart(),
                s.getMultiplierAfterHours(), s.getMultiplierWeekend(), s.getMultiplierHoliday(),
                s.getMonthlyHoursDivisor(), s.isOvertimeEnabled(), CalendarService.paidHoursPerDay(s));
    }
}
