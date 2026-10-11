package com.astromyllc.shootingstar.hr.dto.response;

import java.time.LocalDate;

/** source: API (HolidaysMap), RULES (built-in) or ADMIN (added or edited by a person); null on rows from before sources were recorded. */
public record HolidayResponse(String id, int year, String name, LocalDate statutoryDate,
                              LocalDate observedDate, String observedDay, String type, String note, String source) {
}
