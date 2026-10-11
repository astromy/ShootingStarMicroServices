package com.astromyllc.shootingstar.hr.model;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "work_hours_settings")
public class WorkHoursSettings {
    @Id
    private String id;
    @Indexed
    private String institutionCode;
    // ISO day numbers, 1 = Monday ... 7 = Sunday
    private List<Integer> workDays;
    // HH:mm
    private String startTime;
    // HH:mm
    private String endTime;
    // unpaid break inside the working day (for expected hours)
    private int breakMinutes;
    // a day with less overtime than this counts as none
    private int minOvertimeMinutes;
    // overtime is rounded DOWN to a multiple of this
    private int roundToMinutes;
    // also count time worked before opening time
    private boolean countBeforeStart;
    private double multiplierAfterHours;
    private double multiplierWeekend;
    private double multiplierHoliday;
    // hourly rate = basic salary / this
    private double monthlyHoursDivisor;
    private boolean overtimeEnabled;
    private String updatedBy;
    private Instant updatedAt;

    public WorkHoursSettings() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public List<Integer> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(List<Integer> workDays) {
        this.workDays = workDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }

    public int getMinOvertimeMinutes() {
        return minOvertimeMinutes;
    }

    public void setMinOvertimeMinutes(int minOvertimeMinutes) {
        this.minOvertimeMinutes = minOvertimeMinutes;
    }

    public int getRoundToMinutes() {
        return roundToMinutes;
    }

    public void setRoundToMinutes(int roundToMinutes) {
        this.roundToMinutes = roundToMinutes;
    }

    public boolean isCountBeforeStart() {
        return countBeforeStart;
    }

    public void setCountBeforeStart(boolean countBeforeStart) {
        this.countBeforeStart = countBeforeStart;
    }

    public double getMultiplierAfterHours() {
        return multiplierAfterHours;
    }

    public void setMultiplierAfterHours(double multiplierAfterHours) {
        this.multiplierAfterHours = multiplierAfterHours;
    }

    public double getMultiplierWeekend() {
        return multiplierWeekend;
    }

    public void setMultiplierWeekend(double multiplierWeekend) {
        this.multiplierWeekend = multiplierWeekend;
    }

    public double getMultiplierHoliday() {
        return multiplierHoliday;
    }

    public void setMultiplierHoliday(double multiplierHoliday) {
        this.multiplierHoliday = multiplierHoliday;
    }

    public double getMonthlyHoursDivisor() {
        return monthlyHoursDivisor;
    }

    public void setMonthlyHoursDivisor(double monthlyHoursDivisor) {
        this.monthlyHoursDivisor = monthlyHoursDivisor;
    }

    public boolean isOvertimeEnabled() {
        return overtimeEnabled;
    }

    public void setOvertimeEnabled(boolean overtimeEnabled) {
        this.overtimeEnabled = overtimeEnabled;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
