package com.astromyllc.shootingstar.hr.dto.response;

public record ManHoursRow(String staffCode, String staffName, int workingDays, int holidayDays, int leaveDays,
                          int expectedDays, double expectedHours, int daysPresent, double workedHours,
                          double overtimeApprovedHours, double overtimePendingHours) {
}
