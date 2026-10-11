package com.astromyllc.shootingstar.hr.calc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

/**
 * One institution's working week plus its holiday calendar. The single place
 * that answers "is this a working day?" so leave, man-hours and overtime can
 * never disagree about it.
 */
public final class WorkCalendar {
    private final Set<DayOfWeek> workDays;
    private final Map<LocalDate, String> holidays; // observed date -> name

    public WorkCalendar(Set<DayOfWeek> workDays, Map<LocalDate, String> holidays) {
        this.workDays = workDays == null || workDays.isEmpty()
                ? Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
                : Set.copyOf(workDays);
        this.holidays = holidays == null ? Map.of() : Map.copyOf(holidays);
    }

    public Set<DayOfWeek> workDays() {
        return workDays;
    }

    public boolean isHoliday(LocalDate d) {
        return holidays.containsKey(d);
    }

    public String holidayName(LocalDate d) {
        return holidays.get(d);
    }

    public boolean isWorkDayOfWeek(LocalDate d) {
        return workDays.contains(d.getDayOfWeek());
    }

    /** A normal working day: in the working week and not a holiday. */
    public boolean isWorkingDay(LocalDate d) {
        return isWorkDayOfWeek(d) && !isHoliday(d);
    }

    /** Working days from start to end inclusive (0 when end is before start). */
    public int workingDaysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) return 0;
        int n = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (isWorkingDay(d)) n++;
        }
        return n;
    }

    /** Calendar days from start to end inclusive (0 when end is before start). */
    public static int calendarDaysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) return 0;
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }
}
