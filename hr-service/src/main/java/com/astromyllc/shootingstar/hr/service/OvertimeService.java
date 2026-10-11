package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.calc.OvertimeClassifier;
import com.astromyllc.shootingstar.hr.calc.WorkCalendar;
import com.astromyllc.shootingstar.hr.dto.request.OvertimeDecisionRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimeDetectRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimePayrollRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimeQueryRequest;
import com.astromyllc.shootingstar.hr.dto.response.CountResponse;
import com.astromyllc.shootingstar.hr.dto.response.DetectResponse;
import com.astromyllc.shootingstar.hr.dto.response.OvertimePayrollBatch;
import com.astromyllc.shootingstar.hr.dto.response.OvertimePayrollItem;
import com.astromyllc.shootingstar.hr.dto.response.OvertimeResponse;
import com.astromyllc.shootingstar.hr.exception.HrValidationException;
import com.astromyllc.shootingstar.hr.model.ClockEvent;
import com.astromyllc.shootingstar.hr.model.OvertimeRecord;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.model.WorkHoursSettings;
import com.astromyllc.shootingstar.hr.repository.ClockEventRepository;
import com.astromyllc.shootingstar.hr.repository.OvertimeRecordRepository;
import com.astromyllc.shootingstar.hr.utils.StaffDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Overtime from Pulse clock events: detected automatically, then held as
 * PENDING until someone approves it. Only APPROVED overtime that no payroll
 * run has taken yet is ever handed to Finance.
 */
@Service
public class OvertimeService {

    private static final Logger log = LoggerFactory.getLogger(OvertimeService.class);

    /** A session longer than this is a missed clock-out, not a shift. */
    static final int MAX_SESSION_HOURS = 16;
    private static final int MAX_SCAN_DAYS = 93;

    public static final String PENDING = "PENDING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";

    private final OvertimeRecordRepository records;
    private final ClockEventRepository clockEvents;
    private final CalendarService calendars;

    // Two clock-outs landing together must not create the same record twice.
    private final Object detectLock = new Object();

    public OvertimeService(OvertimeRecordRepository records, ClockEventRepository clockEvents,
                           CalendarService calendars) {
        this.records = records;
        this.clockEvents = clockEvents;
        this.calendars = calendars;
    }

    // ── Detection ───────────────────────────────────────────────────────────

    /** On-demand scan of a period, e.g. to catch up on days before overtime was switched on. */
    public DetectResponse detect(OvertimeDetectRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        LocalDate to = r.toDate() != null ? r.toDate() : LocalDate.now(CalendarService.ZONE);
        LocalDate from = r.fromDate() != null ? r.fromDate() : to.minusDays(30);
        if (from.isAfter(to)) throw new HrValidationException("The start date is after the end date.");
        if (from.plusDays(MAX_SCAN_DAYS).isBefore(to)) {
            throw new HrValidationException("Check up to about three months at a time.");
        }
        String staffCode = null;
        if (r.staffCode() != null && !r.staffCode().isBlank()) {
            Staff staff = StaffDirectory.find(code, r.staffCode())
                    .orElseThrow(() -> new HrValidationException("No staff member found for " + r.staffCode() + "."));
            staffCode = staff.getStaffCode();
        }
        return scan(code, staffCode, from, to);
    }

    /** Called when someone clocks out, so overtime appears without anyone asking for it. */
    public void detectForStaffDay(String institutionCode, String staffCode, LocalDate day) {
        try {
            // A shift can start the evening before, so look at the day before too.
            scan(institutionCode, staffCode, day.minusDays(1), day);
        } catch (RuntimeException e) {
            // Never let overtime bookkeeping break a clock-out.
            log.warn("Overtime detection failed for {} on {}: {}", staffCode, day, e.getMessage());
        }
    }

    private DetectResponse scan(String code, String staffFilter, LocalDate from, LocalDate to) {
        WorkHoursSettings settings = calendars.settings(code);
        if (!settings.isOvertimeEnabled()) {
            return new DetectResponse(0, 0, 0, 0, 0, 0,
                    "Overtime isn't switched on. Turn it on under Setup > Working Hours.");
        }
        WorkCalendar calendar = calendars.calendar(code, settings);
        OvertimeClassifier.Rules rules = CalendarService.rules(settings);

        // Padded: Between is exclusive, and a shift can cross either edge.
        Instant fromInstant = from.minusDays(1).atStartOfDay(CalendarService.ZONE).toInstant();
        Instant toInstant = to.plusDays(2).atStartOfDay(CalendarService.ZONE).toInstant();
        List<ClockEvent> events = staffFilter == null
                ? clockEvents.findByInstitutionCodeAndTimestampBetween(code, fromInstant, toInstant)
                : clockEvents.findByStaffCodeAndTimestampBetween(staffFilter, fromInstant, toInstant);

        Map<String, List<OvertimeClassifier.ClockPoint>> pointsByStaff = new HashMap<>();
        for (ClockEvent e : events) {
            if (e.getTimestamp() == null || e.getType() == null || e.getStaffCode() == null) continue;
            if (e.getInstitutionCode() != null && !e.getInstitutionCode().equalsIgnoreCase(code)) continue;
            boolean in = "IN".equalsIgnoreCase(e.getType());
            if (!in && !"OUT".equalsIgnoreCase(e.getType())) continue;
            pointsByStaff.computeIfAbsent(e.getStaffCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>())
                    .add(new OvertimeClassifier.ClockPoint(e.getTimestamp(), in));
        }

        int unmatchedIn = 0;
        int unmatchedOut = 0;
        int overlong = 0;
        Map<String, Staff> staffByKey = new HashMap<>();
        Map<String, List<OvertimeClassifier.Result>> resultsByStaff = new HashMap<>();
        for (Map.Entry<String, List<OvertimeClassifier.ClockPoint>> entry : pointsByStaff.entrySet()) {
            Staff staff = StaffDirectory.find(code, entry.getKey()).orElse(null);
            if (staff == null) continue; // no longer on the staff list
            OvertimeClassifier.Pairing pairing = OvertimeClassifier.pair(entry.getValue(), MAX_SESSION_HOURS);
            unmatchedIn += pairing.unmatchedIn();
            unmatchedOut += pairing.unmatchedOut();
            overlong += pairing.tooLong();

            List<OvertimeClassifier.Result> inRange = new ArrayList<>();
            for (OvertimeClassifier.Result res : OvertimeClassifier.classify(pairing.sessions(), rules, calendar)) {
                if (!res.date().isBefore(from) && !res.date().isAfter(to)) inRange.add(res);
            }
            staffByKey.put(entry.getKey(), staff);
            resultsByStaff.put(entry.getKey(), inRange);
        }

        int created = 0;
        int updated = 0;
        int removed = 0;
        synchronized (detectLock) {
            List<OvertimeRecord> existing = records.findByInstitutionCode(code);
            Map<String, OvertimeRecord> index = new HashMap<>();
            for (OvertimeRecord o : existing) index.put(key(o.getStaffCode(), o.getDate(), o.getCategory()), o);

            Set<String> seen = new HashSet<>();
            List<OvertimeRecord> toSave = new ArrayList<>();
            for (Map.Entry<String, List<OvertimeClassifier.Result>> entry : resultsByStaff.entrySet()) {
                Staff staff = staffByKey.get(entry.getKey());
                for (OvertimeClassifier.Result res : entry.getValue()) {
                    String k = key(staff.getStaffCode(), res.date().toString(), res.category());
                    seen.add(k);
                    OvertimeRecord current = index.get(k);
                    if (current == null) {
                        OvertimeRecord o = new OvertimeRecord();
                        o.setInstitutionCode(code);
                        o.setStaffCode(staff.getStaffCode());
                        o.setStaffName(StaffDirectory.fullName(staff));
                        o.setDate(res.date().toString());
                        o.setCategory(res.category());
                        o.setMinutes(res.minutes());
                        o.setFirstIn(res.firstIn());
                        o.setLastOut(res.lastOut());
                        o.setHolidayName(res.holidayName());
                        o.setStatus(PENDING);
                        o.setDetectedAt(Instant.now());
                        toSave.add(o);
                        created++;
                    } else if (PENDING.equals(current.getStatus()) && current.getMinutes() != res.minutes()) {
                        // Only undecided overtime follows new clock events; a decision is final.
                        current.setMinutes(res.minutes());
                        current.setFirstIn(res.firstIn());
                        current.setLastOut(res.lastOut());
                        toSave.add(current);
                        updated++;
                    }
                }
            }

            // Pending entries that no longer hold (e.g. a holiday was added and the hours
            // are now a different category) are dropped; decided entries never are.
            List<OvertimeRecord> stale = new ArrayList<>();
            for (OvertimeRecord o : existing) {
                if (!PENDING.equals(o.getStatus())) continue;
                if (staffFilter != null && !staffFilter.equalsIgnoreCase(o.getStaffCode())) continue;
                LocalDate d = LocalDate.parse(o.getDate());
                if (d.isBefore(from) || d.isAfter(to)) continue;
                if (!seen.contains(key(o.getStaffCode(), o.getDate(), o.getCategory()))) stale.add(o);
            }
            removed = stale.size();
            records.saveAll(toSave);
            records.deleteAll(stale);
        }

        StringBuilder message = new StringBuilder();
        message.append(created + updated == 0 && removed == 0
                ? "No new overtime found."
                : "Overtime checked: " + created + " new, " + updated + " updated, " + removed + " removed.");
        if (unmatchedIn > 0) {
            message.append(" ").append(unmatchedIn).append(" clock-in(s) have no clock-out yet (or the shift is still running).");
        }
        if (unmatchedOut > 0) {
            message.append(" ").append(unmatchedOut).append(" clock-out(s) have no clock-in.");
        }
        if (overlong > 0) {
            message.append(" ").append(overlong).append(" session(s) over ").append(MAX_SESSION_HOURS)
                    .append(" hours were ignored as a likely missed clock-out.");
        }
        return new DetectResponse(created, updated, removed, unmatchedIn, unmatchedOut, overlong, message.toString());
    }

    // ── Review ──────────────────────────────────────────────────────────────

    public List<OvertimeResponse> list(OvertimeQueryRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        String status = r.status() == null ? "" : r.status().trim().toUpperCase(Locale.ROOT);
        List<OvertimeResponse> out = new ArrayList<>();
        records.findByInstitutionCode(code).stream()
                .filter(o -> status.isEmpty() || "ALL".equals(status) || status.equals(o.getStatus()))
                .filter(o -> r.staffCode() == null || r.staffCode().isBlank() || r.staffCode().equalsIgnoreCase(o.getStaffCode()))
                .filter(o -> {
                    LocalDate d = LocalDate.parse(o.getDate());
                    return (r.fromDate() == null || !d.isBefore(r.fromDate()))
                            && (r.toDate() == null || !d.isAfter(r.toDate()));
                })
                .sorted(Comparator.comparing(OvertimeRecord::getDate).reversed()
                        .thenComparing(o -> o.getStaffName() == null ? "" : o.getStaffName()))
                .forEach(o -> out.add(toResponse(o)));
        return out;
    }

    public List<OvertimeResponse> approve(OvertimeDecisionRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        requireDecider(r.decidedBy());
        if (r.items() == null || r.items().isEmpty()) throw new HrValidationException("Choose the overtime to approve.");

        Map<String, OvertimeRecord> byId = loadDecidable(code, idsOf(r));
        for (OvertimeDecisionRequest.Item item : r.items()) {
            OvertimeRecord o = byId.get(item.id());
            if (item.approvedMinutes() != null && (item.approvedMinutes() <= 0 || item.approvedMinutes() > o.getMinutes())) {
                throw new HrValidationException("You can approve between 1 and " + o.getMinutes()
                        + " minutes for " + o.getStaffName() + " on " + o.getDate() + ".");
            }
        }

        Instant now = Instant.now();
        List<OvertimeRecord> changed = new ArrayList<>();
        for (OvertimeDecisionRequest.Item item : r.items()) {
            OvertimeRecord o = byId.get(item.id());
            o.setStatus(APPROVED);
            o.setApprovedMinutes(item.approvedMinutes() != null ? item.approvedMinutes() : o.getMinutes());
            o.setDecidedBy(r.decidedBy());
            o.setDecidedAt(now);
            o.setNote(r.note());
            changed.add(o);
        }
        return toResponses(records.saveAll(changed));
    }

    public List<OvertimeResponse> reject(OvertimeDecisionRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        requireDecider(r.decidedBy());
        if (r.ids() == null || r.ids().isEmpty()) throw new HrValidationException("Choose the overtime to reject.");

        Map<String, OvertimeRecord> byId = loadDecidable(code, r.ids());
        Instant now = Instant.now();
        List<OvertimeRecord> changed = new ArrayList<>();
        for (String id : r.ids()) {
            OvertimeRecord o = byId.get(id);
            o.setStatus(REJECTED);
            o.setApprovedMinutes(null);
            o.setDecidedBy(r.decidedBy());
            o.setDecidedAt(now);
            o.setNote(r.note());
            changed.add(o);
        }
        return toResponses(records.saveAll(changed));
    }

    // ── Payroll hand-off ────────────────────────────────────────────────────

    /** Approved overtime no payroll run has taken yet, worked on or before toDate, plus the rates. */
    public OvertimePayrollBatch payrollBatch(OvertimePayrollRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        if (r.toDate() == null) throw new HrValidationException("Say which pay month to collect overtime for.");
        WorkHoursSettings s = calendars.settings(code);
        List<OvertimePayrollItem> items = new ArrayList<>();
        if (s.isOvertimeEnabled()) {
            for (OvertimeRecord o : records.findByInstitutionCode(code)) {
                if (!APPROVED.equals(o.getStatus()) || o.getPayrollRef() != null) continue;
                LocalDate d = LocalDate.parse(o.getDate());
                if (d.isAfter(r.toDate())) continue;
                int minutes = o.getApprovedMinutes() != null ? o.getApprovedMinutes() : o.getMinutes();
                items.add(new OvertimePayrollItem(o.getId(), o.getStaffCode(), d, o.getCategory(), minutes));
            }
        }
        return new OvertimePayrollBatch(s.isOvertimeEnabled(), s.getMonthlyHoursDivisor(), s.getMultiplierAfterHours(),
                s.getMultiplierWeekend(), s.getMultiplierHoliday(), items);
    }

    /** Marks overtime as taken by payroll runs. All or nothing, so a run never half-locks. */
    public CountResponse lockForPayroll(OvertimePayrollRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        if (r.allocations() == null || r.allocations().isEmpty()) return new CountResponse(0);

        List<String> ids = new ArrayList<>();
        for (OvertimePayrollRequest.Allocation a : r.allocations()) {
            if (a.payrollRef() == null || a.payrollRef().isBlank()) throw new HrValidationException("Missing payroll reference.");
            if (a.recordIds() != null) ids.addAll(a.recordIds());
        }
        Map<String, OvertimeRecord> byId = new HashMap<>();
        for (OvertimeRecord o : records.findAllById(ids)) byId.put(o.getId(), o);
        for (String id : ids) {
            OvertimeRecord o = byId.get(id);
            if (o == null || !code.equalsIgnoreCase(o.getInstitutionCode())) {
                throw new HrValidationException("Some overtime in this run no longer exists. Run payroll again.");
            }
            if (!APPROVED.equals(o.getStatus()) || o.getPayrollRef() != null) {
                throw new HrValidationException("Overtime for " + o.getStaffName() + " on " + o.getDate()
                        + " changed or was already paid. Run payroll again.");
            }
        }
        List<OvertimeRecord> changed = new ArrayList<>();
        for (OvertimePayrollRequest.Allocation a : r.allocations()) {
            if (a.recordIds() == null) continue;
            for (String id : a.recordIds()) {
                OvertimeRecord o = byId.get(id);
                o.setPayrollRef(a.payrollRef());
                changed.add(o);
            }
        }
        records.saveAll(changed);
        return new CountResponse(changed.size());
    }

    /** Gives overtime back when a pending payroll run is removed. */
    public CountResponse releaseFromPayroll(OvertimePayrollRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        if (r.payrollRefs() == null || r.payrollRefs().isEmpty()) return new CountResponse(0);
        List<OvertimeRecord> held = records.findByInstitutionCodeAndPayrollRefIn(code, r.payrollRefs());
        for (OvertimeRecord o : held) o.setPayrollRef(null);
        records.saveAll(held);
        return new CountResponse(held.size());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private static String key(String staffCode, String date, String category) {
        return (staffCode == null ? "" : staffCode.toLowerCase(Locale.ROOT)) + "|" + date + "|" + category;
    }

    private static void requireDecider(String decidedBy) {
        if (decidedBy == null || decidedBy.isBlank()) {
            throw new HrValidationException("Your login couldn't be identified. Sign in again.");
        }
    }

    private static List<String> idsOf(OvertimeDecisionRequest r) {
        List<String> ids = new ArrayList<>();
        for (OvertimeDecisionRequest.Item item : r.items()) ids.add(item.id());
        return ids;
    }

    /** Every id must exist in this school and still be waiting for a decision, or nothing changes. */
    private Map<String, OvertimeRecord> loadDecidable(String code, List<String> ids) {
        Map<String, OvertimeRecord> byId = new HashMap<>();
        for (OvertimeRecord o : records.findAllById(ids)) byId.put(o.getId(), o);
        for (String id : ids) {
            OvertimeRecord o = byId.get(id);
            if (o == null || !code.equalsIgnoreCase(o.getInstitutionCode())) {
                throw new HrValidationException("Some of that overtime no longer exists. Refresh and try again.");
            }
            if (!PENDING.equals(o.getStatus())) {
                throw new HrValidationException("Overtime for " + o.getStaffName() + " on " + o.getDate()
                        + " has already been " + o.getStatus().toLowerCase(Locale.ROOT) + ".");
            }
        }
        return byId;
    }

    private static List<OvertimeResponse> toResponses(List<OvertimeRecord> list) {
        List<OvertimeResponse> out = new ArrayList<>();
        for (OvertimeRecord o : list) out.add(toResponse(o));
        return out;
    }

    private static OvertimeResponse toResponse(OvertimeRecord o) {
        LocalDate date = LocalDate.parse(o.getDate());
        return new OvertimeResponse(o.getId(), o.getStaffCode(), o.getStaffName(), date,
                date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH), o.getCategory(),
                label(o.getCategory()), o.getHolidayName(), o.getMinutes(), o.getApprovedMinutes(), o.getFirstIn(),
                o.getLastOut(), o.getStatus(), o.getDecidedBy(), o.getDecidedAt(), o.getNote(), o.getPayrollRef());
    }

    private static String label(String category) {
        if (OvertimeClassifier.PUBLIC_HOLIDAY.equals(category)) return "Public holiday";
        if (OvertimeClassifier.WEEKEND.equals(category)) return "Weekend / rest day";
        return "After hours";
    }
}
