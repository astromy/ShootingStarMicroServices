package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.calc.OvertimeClassifier;
import com.astromyllc.shootingstar.hr.calc.WorkCalendar;
import com.astromyllc.shootingstar.hr.model.PublicHoliday;
import com.astromyllc.shootingstar.hr.model.WorkHoursSettings;
import com.astromyllc.shootingstar.hr.repository.PublicHolidayRepository;
import com.astromyllc.shootingstar.hr.repository.WorkHoursSettingsRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One place that turns an institution's saved working week and holiday
 * calendar into the objects the rest of HR calculates with. Leave, man-hours
 * and overtime all go through here, so they cannot disagree about what a
 * working day is.
 */
@Service
public class CalendarService {

    /** Ghana is on GMT all year (no daylight saving). */
    public static final ZoneId ZONE = ZoneId.of("Africa/Accra");

    private final PublicHolidayRepository holidayRepository;
    private final WorkHoursSettingsRepository settingsRepository;

    public CalendarService(PublicHolidayRepository holidayRepository, WorkHoursSettingsRepository settingsRepository) {
        this.holidayRepository = holidayRepository;
        this.settingsRepository = settingsRepository;
    }

    /** The saved settings, or the defaults on offer when nothing is saved yet. */
    public WorkHoursSettings settings(String institutionCode) {
        return settingsRepository.findByInstitutionCode(institutionCode).orElseGet(() -> defaults(institutionCode));
    }

    public boolean isConfigured(String institutionCode) {
        return settingsRepository.findByInstitutionCode(institutionCode).isPresent();
    }

    public static WorkHoursSettings defaults(String institutionCode) {
        WorkHoursSettings s = new WorkHoursSettings();
        s.setInstitutionCode(institutionCode);
        s.setWorkDays(List.of(1, 2, 3, 4, 5));
        s.setStartTime("08:00");
        s.setEndTime("17:00");
        s.setBreakMinutes(60);
        s.setMinOvertimeMinutes(30);
        s.setRoundToMinutes(15);
        s.setCountBeforeStart(false);
        s.setMultiplierAfterHours(1.5);
        s.setMultiplierWeekend(2.0);
        s.setMultiplierHoliday(2.0);
        s.setMonthlyHoursDivisor(173.33);
        s.setOvertimeEnabled(false);
        return s;
    }

    public WorkCalendar calendar(String institutionCode) {
        return calendar(institutionCode, settings(institutionCode));
    }

    public WorkCalendar calendar(String institutionCode, WorkHoursSettings settings) {
        Map<LocalDate, String> observed = new HashMap<>();
        for (PublicHoliday h : holidayRepository.findByInstitutionCode(institutionCode)) {
            if (h.getObservedDate() != null && !h.getObservedDate().isBlank()) {
                observed.put(LocalDate.parse(h.getObservedDate()), h.getName());
            }
        }
        return new WorkCalendar(toDays(settings.getWorkDays()), observed);
    }

    public static Set<DayOfWeek> toDays(List<Integer> isoDays) {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        if (isoDays != null) {
            for (Integer n : isoDays) {
                if (n != null && n >= 1 && n <= 7) days.add(DayOfWeek.of(n));
            }
        }
        return days;
    }

    public static OvertimeClassifier.Rules rules(WorkHoursSettings s) {
        return new OvertimeClassifier.Rules(LocalTime.parse(s.getStartTime()), LocalTime.parse(s.getEndTime()),
                s.getMinOvertimeMinutes(), s.getRoundToMinutes(), s.isCountBeforeStart(), ZONE);
    }

    /** Length of the paid working day: opening to closing, less the unpaid break. */
    public static double paidHoursPerDay(WorkHoursSettings s) {
        long minutes = Duration.between(LocalTime.parse(s.getStartTime()), LocalTime.parse(s.getEndTime())).toMinutes()
                - s.getBreakMinutes();
        return Math.max(0, minutes) / 60.0;
    }
}
