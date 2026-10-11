package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.calc.WorkCalendar;
import com.astromyllc.shootingstar.hr.dto.request.LeaveApplyRequest;
import com.astromyllc.shootingstar.hr.dto.request.LeaveDecisionRequest;
import com.astromyllc.shootingstar.hr.dto.request.LeaveQueryRequest;
import com.astromyllc.shootingstar.hr.dto.response.LeaveResponse;
import com.astromyllc.shootingstar.hr.exception.HrValidationException;
import com.astromyllc.shootingstar.hr.model.LeaveRequest;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.repository.LeaveRequestRepository;
import com.astromyllc.shootingstar.hr.utils.StaffDirectory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Leave requests, approvals and balances.
 *
 * Days are counted on the institution's working calendar: weekends outside the
 * working week and public holidays are not leave days, so a request across
 * Easter doesn't spend leave on Good Friday. A leave type the admin has set to
 * count calendar days (maternity / paternity by default) is a continuous period,
 * so every day in it counts. How many days each type allows is the school's own
 * setting (LeaveEntitlementService).
 */
@Service
public class LeaveService {

    private static final String PENDING = "Pending";
    private static final String APPROVED = "Approved";
    private static final String REJECTED = "Rejected";

    private final LeaveRequestRepository repository;
    private final CalendarService calendars;
    private final LeaveEntitlementService entitlements;

    public LeaveService(LeaveRequestRepository repository, CalendarService calendars, LeaveEntitlementService entitlements) {
        this.repository = repository;
        this.calendars = calendars;
        this.entitlements = entitlements;
    }

    public LeaveResponse apply(LeaveApplyRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        Staff staff = resolveStaff(code, r.employeeId(), r.actorStaffCode());
        String type = LeaveEntitlementService.canonical(r.leaveType());
        if (entitlements.days(code, type) <= 0) {
            throw new HrValidationException("Your school doesn't offer " + type + " leave.");
        }
        if (r.startDate() == null || r.endDate() == null) throw new HrValidationException("Choose the first and last day of leave.");
        if (r.endDate().isBefore(r.startDate())) throw new HrValidationException("The last day can't be before the first day.");
        if (ChronoUnit.DAYS.between(r.startDate(), r.endDate()) > 400) throw new HrValidationException("That leave period is too long.");

        WorkCalendar calendar = calendars.calendar(code);
        int days = countDays(code, type, r.startDate(), r.endDate(), calendar);
        if (days <= 0) {
            throw new HrValidationException("Those dates are all weekends or public holidays, so no leave is needed.");
        }

        for (LeaveRequest other : repository.findByInstitutionCodeAndStaffCode(code, staff.getStaffCode())) {
            if (!PENDING.equals(other.getStatus()) && !APPROVED.equals(other.getStatus())) continue;
            LocalDate s = LocalDate.parse(other.getStartDate());
            LocalDate e = LocalDate.parse(other.getEndDate());
            if (!r.startDate().isAfter(e) && !r.endDate().isBefore(s)) {
                throw new HrValidationException("There is already a " + other.getStatus().toLowerCase(Locale.ROOT)
                        + " leave request from " + s + " to " + e + " that overlaps these dates.");
            }
        }

        int year = r.startDate().getYear();
        int left = remaining(code, staff.getStaffCode(), type, year, true);
        if (days > left) {
            throw new HrValidationException("Only " + left + " day(s) of " + type + " leave are left for " + year
                    + ", and this request needs " + days + ".");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setInstitutionCode(code);
        leave.setStaffCode(staff.getStaffCode());
        leave.setStaffName(StaffDirectory.fullName(staff));
        leave.setLeaveType(type);
        leave.setStartDate(r.startDate().toString());
        leave.setEndDate(r.endDate().toString());
        leave.setDays(days);
        leave.setReason(r.reason());
        leave.setStatus(PENDING);
        leave.setAppliedOn(Instant.now());
        return toResponse(repository.save(leave));
    }

    public LeaveResponse decide(LeaveDecisionRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        if (r.actor() == null || r.actor().isBlank()) throw new HrValidationException("Your login couldn't be identified. Sign in again.");
        String status = r.status() == null ? "" : r.status().trim().toLowerCase(Locale.ROOT);
        if (!status.equals("approved") && !status.equals("rejected")) {
            throw new HrValidationException("Leave can only be approved or rejected.");
        }
        LeaveRequest leave = repository.findById(r.leaveId() == null ? "" : r.leaveId())
                .filter(l -> code.equalsIgnoreCase(l.getInstitutionCode()))
                .orElseThrow(() -> new HrValidationException("That leave request no longer exists."));
        if (!PENDING.equals(leave.getStatus())) {
            throw new HrValidationException("This request has already been " + leave.getStatus().toLowerCase(Locale.ROOT) + ".");
        }
        leave.setStatus(status.equals("approved") ? APPROVED : REJECTED);
        leave.setDecidedBy(r.actor());
        leave.setDecidedOn(Instant.now());
        leave.setRemarks(r.remarks());
        return toResponse(repository.save(leave));
    }

    /** employeeId blank = everyone in the school (the approval queue); otherwise that one person. */
    public List<LeaveResponse> list(LeaveQueryRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        List<LeaveRequest> found;
        if (r.employeeId() == null || r.employeeId().isBlank()) {
            found = repository.findByInstitutionCode(code);
        } else {
            Staff staff = resolveStaff(code, r.employeeId(), r.actorStaffCode());
            found = repository.findByInstitutionCodeAndStaffCode(code, staff.getStaffCode());
        }
        List<LeaveResponse> out = new ArrayList<>();
        found.stream()
                .sorted(Comparator.comparing(LeaveRequest::getAppliedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                .forEach(l -> out.add(toResponse(l)));
        return out;
    }

    /** Days left per leave type for the year - the keys the Leave screen's balance cards read. */
    public Map<String, Integer> balance(LeaveQueryRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        Staff staff = resolveStaff(code, r.employeeId(), r.actorStaffCode());
        int year = r.year() != null ? r.year() : LocalDate.now(CalendarService.ZONE).getYear();
        Map<String, Integer> out = new LinkedHashMap<>();
        for (String type : LeaveEntitlementService.orderedTypes()) {
            out.put(type, remaining(code, staff.getStaffCode(), type, year, false));
        }
        return out;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /** Approved leave of a staff member that covers a given day. */
    public List<LeaveRequest> approvedFor(String code, String staffCode) {
        List<LeaveRequest> out = new ArrayList<>();
        for (LeaveRequest l : repository.findByInstitutionCodeAndStaffCode(code, staffCode)) {
            if (APPROVED.equals(l.getStatus())) out.add(l);
        }
        return out;
    }

    public List<LeaveRequest> approvedForInstitution(String code) {
        List<LeaveRequest> out = new ArrayList<>();
        for (LeaveRequest l : repository.findByInstitutionCode(code)) {
            if (APPROVED.equals(l.getStatus())) out.add(l);
        }
        return out;
    }

    private int remaining(String code, String staffCode, String type, int year, boolean includePending) {
        int used = 0;
        for (LeaveRequest l : repository.findByInstitutionCodeAndStaffCode(code, staffCode)) {
            if (!type.equals(l.getLeaveType())) continue;
            if (LocalDate.parse(l.getStartDate()).getYear() != year) continue;
            if (APPROVED.equals(l.getStatus()) || (includePending && PENDING.equals(l.getStatus()))) used += l.getDays();
        }
        return Math.max(0, entitlements.days(code, type) - used);
    }

    private int countDays(String code, String type, LocalDate start, LocalDate end, WorkCalendar calendar) {
        return entitlements.calendarDays(code, type)
                ? WorkCalendar.calendarDaysBetween(start, end)
                : calendar.workingDaysBetween(start, end);
    }

    /** employeeId blank or "me" means the signed-in person, whose staff code the proxy supplies. */
    static Staff resolveStaff(String code, String employeeId, String actorStaffCode) {
        boolean self = employeeId == null || employeeId.isBlank() || "me".equalsIgnoreCase(employeeId.trim());
        String wanted = self ? actorStaffCode : employeeId;
        if (wanted == null || wanted.isBlank()) {
            throw new HrValidationException("Your login isn't linked to a staff record. Ask HR to add you.");
        }
        return StaffDirectory.find(code, wanted)
                .orElseThrow(() -> new HrValidationException("No staff record was found for " + wanted + "."));
    }

    private static LeaveResponse toResponse(LeaveRequest l) {
        return new LeaveResponse(l.getId(), l.getStaffCode(), l.getStaffName(), l.getLeaveType(),
                LocalDate.parse(l.getStartDate()), LocalDate.parse(l.getEndDate()), l.getDays(), l.getReason(),
                l.getStatus(), l.getAppliedOn(), l.getDecidedBy(), l.getRemarks());
    }
}
