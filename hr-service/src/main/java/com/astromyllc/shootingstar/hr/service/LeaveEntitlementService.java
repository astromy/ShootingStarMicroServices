package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.LeaveEntitlementRequest;
import com.astromyllc.shootingstar.hr.dto.response.LeaveEntitlementResponse;
import com.astromyllc.shootingstar.hr.exception.HrValidationException;
import com.astromyllc.shootingstar.hr.model.LeaveEntitlement;
import com.astromyllc.shootingstar.hr.repository.LeaveEntitlementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * How much leave of each type a staff member gets in a year, set by the school's admin
 * (Setup > Leave Entitlements). Until a type is saved, the suggested values apply - the same
 * figures the Leave screen has always shown.
 *
 * "Count weekends and holidays" is for leave that runs as one continuous period (maternity /
 * paternity): every calendar day inside it is used. For the others only working days are used,
 * so weekends and public holidays never cost leave.
 */
@Service
public class LeaveEntitlementService {

    /** The leave types the Leave screen offers, in display order. */
    public static final Set<String> TYPES = Set.of("Casual", "Sick", "Earned", "Maternity");

    private static final Map<String, String> LABELS = new LinkedHashMap<>();
    private static final Map<String, Integer> DEFAULT_DAYS = new LinkedHashMap<>();

    static {
        LABELS.put("Casual", "Casual leave");
        LABELS.put("Sick", "Sick leave");
        LABELS.put("Earned", "Earned (annual) leave");
        LABELS.put("Maternity", "Maternity / Paternity leave");
        DEFAULT_DAYS.put("Casual", 12);
        DEFAULT_DAYS.put("Sick", 10);
        DEFAULT_DAYS.put("Earned", 15);
        DEFAULT_DAYS.put("Maternity", 84);
    }

    private final LeaveEntitlementRepository repository;

    public LeaveEntitlementService(LeaveEntitlementRepository repository) {
        this.repository = repository;
    }

    /** All four types, in display order, saved or suggested. */
    public List<LeaveEntitlementResponse> get(String institutionCode) {
        String code = HolidayService.requireInstitution(institutionCode);
        Map<String, LeaveEntitlement> saved = byType(code);
        List<LeaveEntitlementResponse> out = new ArrayList<>();
        for (String type : LABELS.keySet()) {
            LeaveEntitlement e = saved.get(type);
            out.add(e == null
                    ? new LeaveEntitlementResponse(type, LABELS.get(type), DEFAULT_DAYS.get(type), "Maternity".equals(type), false)
                    : new LeaveEntitlementResponse(type, LABELS.get(type), e.getDays(), e.isCalendarDays(), true));
        }
        return out;
    }

    public List<LeaveEntitlementResponse> save(LeaveEntitlementRequest r) {
        String code = HolidayService.requireInstitution(r.institutionCode());
        if (r.items() == null || r.items().isEmpty()) throw new HrValidationException("Enter the days for at least one leave type.");

        Map<String, LeaveEntitlement> saved = byType(code);
        List<LeaveEntitlement> toSave = new ArrayList<>();
        for (LeaveEntitlementRequest.Item item : r.items()) {
            String type = canonical(item.leaveType());
            if (item.days() == null || item.days() < 0 || item.days() > 366) {
                throw new HrValidationException(LABELS.get(type) + ": enter a number of days from 0 to 366 (0 means your school doesn't offer it).");
            }
            LeaveEntitlement e = saved.get(type);
            if (e == null) {
                e = new LeaveEntitlement();
                e.setInstitutionCode(code);
                e.setLeaveType(type);
                e.setCalendarDays("Maternity".equals(type));
            }
            e.setDays(item.days());
            if (item.calendarDays() != null) e.setCalendarDays(item.calendarDays());
            e.setUpdatedBy(r.updatedBy());
            e.setUpdatedAt(Instant.now());
            toSave.add(e);
        }
        repository.saveAll(toSave);
        return get(code);
    }

    public int days(String institutionCode, String type) {
        LeaveEntitlement e = byType(institutionCode).get(type);
        return e == null ? DEFAULT_DAYS.get(type) : e.getDays();
    }

    public boolean calendarDays(String institutionCode, String type) {
        LeaveEntitlement e = byType(institutionCode).get(type);
        return e == null ? "Maternity".equals(type) : e.isCalendarDays();
    }

    /** The names the Leave screen uses, in order. */
    public static List<String> orderedTypes() {
        return new ArrayList<>(LABELS.keySet());
    }

    public static String canonical(String value) {
        if (value != null) {
            for (String type : LABELS.keySet()) {
                if (type.equalsIgnoreCase(value.trim())) return type;
            }
        }
        throw new HrValidationException("Choose Casual, Sick, Earned or Maternity leave.");
    }

    private Map<String, LeaveEntitlement> byType(String institutionCode) {
        Map<String, LeaveEntitlement> map = new LinkedHashMap<>();
        for (LeaveEntitlement e : repository.findByInstitutionCode(institutionCode)) map.put(e.getLeaveType(), e);
        return map;
    }
}
