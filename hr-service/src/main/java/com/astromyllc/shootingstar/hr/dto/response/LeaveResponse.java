package com.astromyllc.shootingstar.hr.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record LeaveResponse(String leaveId, String employeeId, String employeeName, String leaveType,
                            LocalDate startDate, LocalDate endDate, int days, String reason, String status,
                            Instant appliedOn, String decidedBy, String remarks) {
}
