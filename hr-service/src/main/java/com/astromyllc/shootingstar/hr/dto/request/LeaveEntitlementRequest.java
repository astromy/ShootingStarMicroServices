package com.astromyllc.shootingstar.hr.dto.request;

import java.util.List;

/** Only the types listed are changed; the others keep what they have. */
public record LeaveEntitlementRequest(String institutionCode, List<Item> items, String updatedBy) {
    public record Item(String leaveType, Integer days, Boolean calendarDays) {
    }
}
