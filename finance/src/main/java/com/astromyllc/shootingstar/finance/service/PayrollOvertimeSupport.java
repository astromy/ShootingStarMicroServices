package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.model.Salaries;
import com.astromyllc.shootingstar.finance.model.SalaryItem;
import com.astromyllc.shootingstar.finance.utils.OvertimePricing;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Everything payroll needs to know about overtime, kept out of PayrollService
 * so a payroll run only gains a few lines:
 *   collect  - approved, unpaid overtime from HR for the month being paid;
 *   withOvertime - adds the priced lines to a staff member's salary lines;
 *   lock / release - tells HR which runs took the overtime, or gives it back.
 */
@Component
public class PayrollOvertimeSupport {

    private static final DateTimeFormatter PERIOD = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private static final String PREFIX = "SAL-";

    public record Collected(Map<String, List<OvertimeClient.Item>> byStaff, OvertimePricing.Rates rates) {
        static Collected none() {
            return new Collected(Map.of(), new OvertimePricing.Rates(0, 0, 0, 0));
        }
    }

    private final OvertimeClient client;

    public PayrollOvertimeSupport(OvertimeClient client) {
        this.client = client;
    }

    /**
     * Overtime worked up to the end of the pay month. Approved overtime from an earlier month that
     * no run has paid yet (approved late) is included, so it rolls forward instead of being lost.
     */
    public Collected collect(String institutionCode, String payPeriod) {
        YearMonth month;
        try {
            month = YearMonth.parse(payPeriod.trim(), PERIOD);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("The pay period must be a month and year, like \"September 2026\".");
        }
        OvertimeClient.Batch batch = client.fetch(institutionCode, month.atEndOfMonth());
        if (!batch.enabled() || batch.items() == null || batch.items().isEmpty()) return Collected.none();

        Map<String, List<OvertimeClient.Item>> byStaff = new HashMap<>();
        for (OvertimeClient.Item item : batch.items()) {
            byStaff.computeIfAbsent(item.staffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(item);
        }
        return new Collected(byStaff, new OvertimePricing.Rates(batch.monthlyHoursDivisor(),
                batch.multiplierAfterHours(), batch.multiplierWeekend(), batch.multiplierHoliday()));
    }

    /** The staff member's salary lines plus their overtime; usedIds receives the overtime taken. */
    public List<SalaryItem> withOvertime(double basic, List<SalaryItem> profileItems, Collected collected,
                                         String staffId, List<String> usedIds) {
        List<OvertimeClient.Item> mine = collected.byStaff().getOrDefault(staffId.toLowerCase(Locale.ROOT), List.of());
        if (mine.isEmpty()) return profileItems;

        Map<String, Integer> minutes = new HashMap<>();
        for (OvertimeClient.Item item : mine) minutes.merge(item.category(), item.minutes(), Integer::sum);

        List<OvertimePricing.Line> lines = OvertimePricing.price(basic, collected.rates(), minutes);
        if (lines.isEmpty()) return profileItems;

        List<SalaryItem> out = new ArrayList<>(profileItems == null ? List.of() : profileItems);
        for (OvertimePricing.Line line : lines) {
            out.add(SalaryItem.builder()
                    .itemName(line.itemName())
                    .itemType("ALLOWANCE")
                    .amount(line.amount())
                    .isPercentage(false)
                    .itemCode(line.itemCode())
                    .build());
        }
        for (OvertimeClient.Item item : mine) usedIds.add(item.id());
        return out;
    }

    /** After the runs are saved: tells HR which run took which overtime. All or nothing. */
    public void lock(String institutionCode, List<Salaries> saved, Map<String, List<String>> usedByStaff) {
        List<OvertimeClient.Allocation> allocations = new ArrayList<>();
        for (Salaries s : saved) {
            List<String> ids = usedByStaff.get(s.getStaffId().toLowerCase(Locale.ROOT));
            if (ids != null && !ids.isEmpty()) allocations.add(new OvertimeClient.Allocation(PREFIX + s.getSalaryId(), ids));
        }
        if (!allocations.isEmpty()) client.lock(institutionCode, allocations);
    }

    /** Gives overtime back to HR when pending runs are removed. Only runs that took overtime need it. */
    public void release(String institutionCode, List<Salaries> removed) {
        List<String> refs = new ArrayList<>();
        for (Salaries s : removed) {
            if (s.getSalaryItems() == null) continue;
            for (SalaryItem item : s.getSalaryItems()) {
                if (item.getItemCode() != null && item.getItemCode().startsWith(OvertimePricing.CODE_PREFIX)) {
                    refs.add(PREFIX + s.getSalaryId());
                    break;
                }
            }
        }
        if (!refs.isEmpty()) client.release(institutionCode, refs);
    }
}
