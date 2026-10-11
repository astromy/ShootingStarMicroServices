package com.astromyllc.shootingstar.hr.dto.request;

import java.time.LocalDate;

public record OvertimeQueryRequest(String institutionCode, String status, LocalDate fromDate,
                                   LocalDate toDate, String staffCode) {
}
