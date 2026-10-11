package com.astromyllc.shootingstar.hr.dto.response;

import java.util.List;

/** What Finance needs to price approved overtime: the rates and the approved, not-yet-paid hours. */
public record OvertimePayrollBatch(boolean enabled, double monthlyHoursDivisor, double multiplierAfterHours,
                                   double multiplierWeekend, double multiplierHoliday,
                                   List<OvertimePayrollItem> items) {
}
