package com.astromyllc.shootingstar.hr.dto.request;

import java.time.LocalDate;

public record LeaveApplyRequest(String institutionCode, String employeeId, String actorStaffCode,
                                String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
}
