package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.model.Staff;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Finds staff in the in-memory staff list, always within one institution. */
public final class StaffDirectory {

    private StaffDirectory() {
    }

    /** identifier may be the staff code or the staff record's id. */
    public static Optional<Staff> find(String institutionCode, String identifier) {
        if (institutionCode == null || identifier == null || identifier.isBlank()) return Optional.empty();
        String wanted = identifier.trim();
        for (Staff s : StaffUtil.staffGlobalList) {
            if (s.getInstitutionCode() == null || !s.getInstitutionCode().equalsIgnoreCase(institutionCode)) continue;
            if (s.getStaffCode() != null && s.getStaffCode().equalsIgnoreCase(wanted)) return Optional.of(s);
            if (s.getId() != null && s.getId().toHexString().equalsIgnoreCase(wanted)) return Optional.of(s);
        }
        return Optional.empty();
    }

    public static List<Staff> inInstitution(String institutionCode) {
        List<Staff> out = new ArrayList<>();
        if (institutionCode == null) return out;
        for (Staff s : StaffUtil.staffGlobalList) {
            if (s.getInstitutionCode() != null && s.getInstitutionCode().equalsIgnoreCase(institutionCode)) out.add(s);
        }
        return out;
    }

    public static String fullName(Staff s) {
        String first = s.getFirstNames() == null ? "" : s.getFirstNames().trim();
        String last = s.getLastName() == null ? "" : s.getLastName().trim();
        return (first + " " + last).trim();
    }
}
