package com.astromyllc.shootingstar.hr.calc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Turns raw Pulse clock events into overtime, with no framework dependencies
 * so the rules can be tested directly.
 *
 *  1. pair() joins IN/OUT events into worked sessions and reports the ones it
 *     could not trust (a missing clock-out must never become a 20-hour shift);
 *  2. classify() splits sessions by calendar day and decides, per day, which
 *     minutes are overtime and of which kind:
 *       PUBLIC_HOLIDAY - any work on an observed holiday (all of it);
 *       WEEKEND        - any work on a day outside the working week (all of it);
 *       AFTER_HOURS    - on a normal working day, time after closing time
 *                        (and before opening time only if countBeforeStart).
 *     A day under minOvertimeMinutes counts as nothing, and the rest is
 *     rounded down to roundToMinutes, so a few stray minutes don't generate a
 *     pay claim.
 */
public final class OvertimeClassifier {

    public static final String AFTER_HOURS = "AFTER_HOURS";
    public static final String WEEKEND = "WEEKEND";
    public static final String PUBLIC_HOLIDAY = "PUBLIC_HOLIDAY";

    public record Rules(LocalTime start, LocalTime end, int minOvertimeMinutes, int roundToMinutes,
                        boolean countBeforeStart, ZoneId zone) {
    }

    public record ClockPoint(Instant at, boolean in) {
    }

    public record Session(Instant in, Instant out) {
    }

    /**
     * @param unmatchedIn  clock-ins never followed by a clock-out (includes a
     *                     shift still in progress)
     * @param unmatchedOut clock-outs with no clock-in before them
     * @param tooLong      sessions longer than the limit, treated as a missed clock-out
     */
    public record Pairing(List<Session> sessions, int unmatchedIn, int unmatchedOut, int tooLong) {
    }

    public record Result(LocalDate date, String category, int minutes, Instant firstIn, Instant lastOut,
                         String holidayName) {
    }

    private record Segment(Instant start, Instant end) {
    }

    private OvertimeClassifier() {
    }

    public static Pairing pair(List<ClockPoint> points, int maxSessionHours) {
        List<ClockPoint> sorted = new ArrayList<>(points == null ? List.of() : points);
        sorted.sort(Comparator.comparing(ClockPoint::at));
        Duration max = Duration.ofHours(maxSessionHours);

        List<Session> sessions = new ArrayList<>();
        Instant open = null;
        int unmatchedIn = 0;
        int unmatchedOut = 0;
        int tooLong = 0;

        for (ClockPoint p : sorted) {
            if (p.in()) {
                if (open == null) {
                    open = p.at();
                } else if (Duration.between(open, p.at()).compareTo(max) > 0) {
                    // The earlier IN is too old to still be a shift: its OUT was
                    // missed. Start again from this one.
                    unmatchedIn++;
                    open = p.at();
                }
                // else: a second tap on IN during a plausible shift - keep the first.
            } else {
                if (open == null) {
                    unmatchedOut++;
                } else {
                    Duration d = Duration.between(open, p.at());
                    if (d.compareTo(max) > 0) {
                        tooLong++;
                    } else if (!d.isNegative() && !d.isZero()) {
                        sessions.add(new Session(open, p.at()));
                    }
                    open = null;
                }
            }
        }
        if (open != null) unmatchedIn++;
        return new Pairing(sessions, unmatchedIn, unmatchedOut, tooLong);
    }

    public static List<Result> classify(List<Session> sessions, Rules rules, WorkCalendar calendar) {
        ZoneId zone = rules.zone();
        Map<LocalDate, List<Segment>> byDate = new TreeMap<>();

        for (Session s : sessions) {
            LocalDate first = s.in().atZone(zone).toLocalDate();
            LocalDate last = s.out().atZone(zone).toLocalDate();
            for (LocalDate d = first; !d.isAfter(last); d = d.plusDays(1)) {
                Instant dayStart = d.atStartOfDay(zone).toInstant();
                Instant dayEnd = d.plusDays(1).atStartOfDay(zone).toInstant();
                Instant from = s.in().isAfter(dayStart) ? s.in() : dayStart;
                Instant to = s.out().isBefore(dayEnd) ? s.out() : dayEnd;
                if (to.isAfter(from)) {
                    byDate.computeIfAbsent(d, k -> new ArrayList<>()).add(new Segment(from, to));
                }
            }
        }

        List<Result> results = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Segment>> entry : byDate.entrySet()) {
            LocalDate d = entry.getKey();
            List<Segment> segments = entry.getValue();

            String category;
            long seconds = 0;
            if (calendar.isHoliday(d)) {
                category = PUBLIC_HOLIDAY;
                for (Segment seg : segments) seconds += Duration.between(seg.start(), seg.end()).getSeconds();
            } else if (!calendar.isWorkDayOfWeek(d)) {
                category = WEEKEND;
                for (Segment seg : segments) seconds += Duration.between(seg.start(), seg.end()).getSeconds();
            } else {
                category = AFTER_HOURS;
                Instant closing = d.atTime(rules.end()).atZone(zone).toInstant();
                Instant opening = d.atTime(rules.start()).atZone(zone).toInstant();
                Instant dayStart = d.atStartOfDay(zone).toInstant();
                Instant dayEnd = d.plusDays(1).atStartOfDay(zone).toInstant();
                for (Segment seg : segments) {
                    seconds += overlapSeconds(seg, closing, dayEnd);
                    if (rules.countBeforeStart()) seconds += overlapSeconds(seg, dayStart, opening);
                }
            }

            int minutes = (int) (seconds / 60);
            if (minutes < rules.minOvertimeMinutes()) continue;
            if (rules.roundToMinutes() > 1) {
                minutes = (minutes / rules.roundToMinutes()) * rules.roundToMinutes();
            }
            if (minutes <= 0) continue;

            Instant firstIn = segments.get(0).start();
            Instant lastOut = segments.get(0).end();
            for (Segment seg : segments) {
                if (seg.start().isBefore(firstIn)) firstIn = seg.start();
                if (seg.end().isAfter(lastOut)) lastOut = seg.end();
            }
            results.add(new Result(d, category, minutes, firstIn, lastOut, calendar.holidayName(d)));
        }
        return results;
    }

    private static long overlapSeconds(Segment seg, Instant from, Instant to) {
        Instant s = seg.start().isAfter(from) ? seg.start() : from;
        Instant e = seg.end().isBefore(to) ? seg.end() : to;
        return e.isAfter(s) ? Duration.between(s, e).getSeconds() : 0;
    }
}
