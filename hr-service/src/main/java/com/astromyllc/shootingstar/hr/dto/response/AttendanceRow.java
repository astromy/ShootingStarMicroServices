package com.astromyllc.shootingstar.hr.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record AttendanceRow(String employeeId, String employeeName, LocalDate date, String status,
                            Instant checkIn, Instant checkOut, double hours) {
}
