package com.astromyllc.shootingstar.hr.dto.request;

import java.time.LocalDate;

/** month is 0-based (January = 0), as the browser sends it. */
public record AttendanceQueryRequest(String institutionCode, String employeeId, String actorStaffCode,
                                     Integer month, Integer year, LocalDate date) {
}
