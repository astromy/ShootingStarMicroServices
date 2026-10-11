package com.astromyllc.shootingstar.hr.dto.request;

import java.time.LocalDate;

public record HolidayRequest(String institutionCode, String id, Integer year, String name,
                             LocalDate statutoryDate, LocalDate observedDate, String type, String note) {
}
