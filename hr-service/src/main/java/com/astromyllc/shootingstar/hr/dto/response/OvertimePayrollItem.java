package com.astromyllc.shootingstar.hr.dto.response;

import java.time.LocalDate;

public record OvertimePayrollItem(String id, String staffCode, LocalDate date, String category, int minutes) {
}
