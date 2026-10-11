package com.astromyllc.shootingstar.hr.dto.request;

/** month is 0-based (January = 0), as the browser sends it. */
public record ManHoursRequest(String institutionCode, Integer month, Integer year, String staffCode) {
}
