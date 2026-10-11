package com.astromyllc.shootingstar.hr.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leave_entitlement")
public class LeaveEntitlement {
    @Id
    private String id;
    @Indexed
    private String institutionCode;
    // Casual | Sick | Earned | Maternity
    private String leaveType;
    // days a staff member may take in a year; 0 = this school doesn't offer it
    private int days;
    // count weekends and public holidays inside the leave (a continuous period, e.g. maternity)
    private boolean calendarDays;
    private String updatedBy;
    private Instant updatedAt;

    public LeaveEntitlement() {
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

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public boolean isCalendarDays() {
        return calendarDays;
    }

    public void setCalendarDays(boolean calendarDays) {
        this.calendarDays = calendarDays;
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
