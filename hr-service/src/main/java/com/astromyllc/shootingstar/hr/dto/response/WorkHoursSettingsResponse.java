package com.astromyllc.shootingstar.hr.dto.response;

import java.util.List;

/** configured = false means nothing is saved yet and these are the defaults on offer. */
public record WorkHoursSettingsResponse(boolean configured, List<Integer> workDays, String startTime,
                                        String endTime, int breakMinutes, int minOvertimeMinutes,
                                        int roundToMinutes, boolean countBeforeStart,
                                        double multiplierAfterHours, double multiplierWeekend,
                                        double multiplierHoliday, double monthlyHoursDivisor,
                                        boolean overtimeEnabled, double paidHoursPerDay) {
}
