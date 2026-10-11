package com.astromyllc.shootingstar.hr.dto.request;

import java.util.List;

public record WorkHoursSettingsRequest(String institutionCode, List<Integer> workDays, String startTime,
                                       String endTime, Integer breakMinutes, Integer minOvertimeMinutes,
                                       Integer roundToMinutes, Boolean countBeforeStart,
                                       Double multiplierAfterHours, Double multiplierWeekend,
                                       Double multiplierHoliday, Double monthlyHoursDivisor,
                                       Boolean overtimeEnabled, String updatedBy) {
}
