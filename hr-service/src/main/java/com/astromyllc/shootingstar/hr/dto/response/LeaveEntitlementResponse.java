package com.astromyllc.shootingstar.hr.dto.response;

/** configured = false means nothing is saved for this type yet and these are the suggested starting values. */
public record LeaveEntitlementResponse(String leaveType, String label, int days, boolean calendarDays, boolean configured) {
}
