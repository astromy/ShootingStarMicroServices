package com.astromyllc.shootingstar.hr.dto.request;

import java.time.LocalDate;

public record OvertimeDetectRequest(String institutionCode, LocalDate fromDate, LocalDate toDate, String staffCode) {
}
