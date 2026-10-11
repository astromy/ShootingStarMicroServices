package com.astromyllc.shootingstar.hr.calc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Suggested Ghana public holidays for a year, used only to PRE-FILL the
 * holiday calendar that an administrator then confirms.
 *
 * Not authoritative, on purpose. Ghana's calendar changes by law and by
 * Executive Instrument:
 *   - the 2025 amendment returned Republic Day (1 July) to statutory status,
 *     reinstated 21 September as Founders' Day and dropped 4 August;
 *   - midweek holidays can be moved to the preceding Friday or following
 *     Monday by Executive Instrument, announced shortly before the day;
 *   - Eid ul-Fitr and Eid ul-Adha depend on sighting of the new moon.
 * None of that can be computed, so the only date logic here is what IS
 * rule-based: fixed dates, Easter, Farmers' Day, and the weekend rule (a
 * holiday on a Saturday or Sunday is observed on the next working day).
 * Eid dates are deliberately not included - add them when announced.
 */
public final class GhanaHolidayRules {

    /** @param observedDate the day staff are actually off; admin may change it */
    public record Draft(String name, LocalDate statutoryDate, LocalDate observedDate) {
    }

    private GhanaHolidayRules() {
    }

    public static List<Draft> suggestedHolidays(int year) {
        List<String> names = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        add(names, dates, "New Year's Day", LocalDate.of(year, 1, 1));
        add(names, dates, "Constitution Day", LocalDate.of(year, 1, 7));
        add(names, dates, "Independence Day", LocalDate.of(year, 3, 6));
        LocalDate easter = easterSunday(year);
        add(names, dates, "Good Friday", easter.minusDays(2));
        add(names, dates, "Easter Monday", easter.plusDays(1));
        add(names, dates, "Labour Day", LocalDate.of(year, 5, 1));
        add(names, dates, "Republic Day", LocalDate.of(year, 7, 1));
        add(names, dates, "Founders' Day", LocalDate.of(year, 9, 21));
        add(names, dates, "Farmers' Day",
                LocalDate.of(year, 12, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.FRIDAY)));
        add(names, dates, "Christmas Day", LocalDate.of(year, 12, 25));
        add(names, dates, "Boxing Day", LocalDate.of(year, 12, 26));

        // Chronological, so when two holidays compete for the same observed
        // day (Christmas and Boxing Day on a weekend) the earlier one keeps it.
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) order.add(i);
        order.sort(Comparator.comparing(dates::get));

        Set<LocalDate> taken = new HashSet<>();
        List<Draft> out = new ArrayList<>();
        for (int i : order) {
            LocalDate statutory = dates.get(i);
            LocalDate observed = statutory;
            while (isWeekend(observed) || taken.contains(observed)) {
                observed = observed.plusDays(1);
            }
            taken.add(observed);
            out.add(new Draft(names.get(i), statutory, observed));
        }
        return out;
    }

    private static void add(List<String> names, List<LocalDate> dates, String name, LocalDate date) {
        names.add(name);
        dates.add(date);
    }

    private static boolean isWeekend(LocalDate d) {
        return d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /** Anonymous Gregorian algorithm (Meeus/Jones/Butcher). */
    public static LocalDate easterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}
