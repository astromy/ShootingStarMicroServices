package com.astromyllc.shootingstar.adminpta.dto.request;

/** Asks for the whole record of one student of one school. */
public class StudentDetailRequest {
    private String institutionCode;
    private String studentId;

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
