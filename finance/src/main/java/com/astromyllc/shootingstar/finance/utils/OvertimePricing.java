package com.astromyllc.shootingstar.finance.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Prices approved overtime:
 *
 *   hourly rate = basic salary / hours-per-month (Working Hours setup)
 *   amount      = hours x hourly rate x the rate for that kind of overtime
 *
 * One line per kind, with FIXED names: ledger posting opens an expense account
 * per allowance name, so a name that carried the hours would add a new account
 * to the books for every different figure.
 */
public final class OvertimePricing {

    // These must match the category names HR sends.
    public static final String AFTER_HOURS = "AFTER_HOURS";
    public static final String WEEKEND = "WEEKEND";
    public static final String PUBLIC_HOLIDAY = "PUBLIC_HOLIDAY";

    public static final String CODE_PREFIX = "OVERTIME_";

    public record Rates(double monthlyHoursDivisor, double afterHours, double weekend, double holiday) {
    }

    public record Line(String itemName, String itemCode, double hours, double multiplier, double amount) {
    }

    private OvertimePricing() {
    }

    public static List<Line> price(double basic, Rates rates, Map<String, Integer> minutesByCategory) {
        if (rates.monthlyHoursDivisor() <= 0) {
            throw new IllegalArgumentException("Set the hours per month used for the overtime hourly rate in Working Hours setup.");
        }
        for (String category : minutesByCategory.keySet()) {
            if (!AFTER_HOURS.equals(category) && !WEEKEND.equals(category) && !PUBLIC_HOLIDAY.equals(category)) {
                throw new IllegalStateException("HR sent an overtime category payroll doesn't know: " + category);
            }
        }
        double hourly = basic / rates.monthlyHoursDivisor();
        List<Line> lines = new ArrayList<>();
        add(lines, hourly, minutesByCategory.getOrDefault(AFTER_HOURS, 0), rates.afterHours(),
                "Overtime - After hours", CODE_PREFIX + AFTER_HOURS);
        add(lines, hourly, minutesByCategory.getOrDefault(WEEKEND, 0), rates.weekend(),
                "Overtime - Weekend", CODE_PREFIX + WEEKEND);
        add(lines, hourly, minutesByCategory.getOrDefault(PUBLIC_HOLIDAY, 0), rates.holiday(),
                "Overtime - Public holiday", CODE_PREFIX + PUBLIC_HOLIDAY);
        return lines;
    }

    private static void add(List<Line> lines, double hourly, int minutes, double multiplier, String name, String code) {
        if (minutes <= 0) return;
        double hours = minutes / 60.0;
        double amount = Math.round(hours * hourly * multiplier * 100.0) / 100.0;
        if (amount <= 0) return;
        lines.add(new Line(name, code, hours, multiplier, amount));
    }
}
