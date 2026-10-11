package com.astromyllc.shootingstar.hr.dto.request;

/** Asks for the whole record of one staff member of one school. */
public class StaffDetailRequest {
    private String institutionCode;
    private String staffCode;

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
}
