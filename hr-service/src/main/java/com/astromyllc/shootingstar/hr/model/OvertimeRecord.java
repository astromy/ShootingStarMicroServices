package com.astromyllc.shootingstar.hr.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "overtime_record")
public class OvertimeRecord {
    @Id
    private String id;
    @Indexed
    private String institutionCode;
    @Indexed
    private String staffCode;
    private String staffName;
    // ISO date the overtime was worked
    private String date;
    // AFTER_HOURS | WEEKEND | PUBLIC_HOLIDAY
    private String category;
    // as detected from clock events
    private int minutes;
    // what the approver allowed (never more than minutes)
    private Integer approvedMinutes;
    private Instant firstIn;
    private Instant lastOut;
    private String holidayName;
    // PENDING | APPROVED | REJECTED
    private String status;
    private Instant detectedAt;
    private String decidedBy;
    private Instant decidedAt;
    private String note;
    // set by Finance once a payroll run has taken this overtime
    private String payrollRef;

    public OvertimeRecord() {
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

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Integer getApprovedMinutes() {
        return approvedMinutes;
    }

    public void setApprovedMinutes(Integer approvedMinutes) {
        this.approvedMinutes = approvedMinutes;
    }

    public Instant getFirstIn() {
        return firstIn;
    }

    public void setFirstIn(Instant firstIn) {
        this.firstIn = firstIn;
    }

    public Instant getLastOut() {
        return lastOut;
    }

    public void setLastOut(Instant lastOut) {
        this.lastOut = lastOut;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(Instant detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPayrollRef() {
        return payrollRef;
    }

    public void setPayrollRef(String payrollRef) {
        this.payrollRef = payrollRef;
    }
}
