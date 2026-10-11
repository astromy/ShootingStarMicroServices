package com.astromyllc.shootingstar.hr.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record OvertimeResponse(String id, String staffCode, String staffName, LocalDate date, String dayName,
                               String category, String categoryLabel, String holidayName, int minutes,
                               Integer approvedMinutes, Instant firstIn, Instant lastOut, String status,
                               String decidedBy, Instant decidedAt, String note, String payrollRef) {
}
