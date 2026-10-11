package com.astromyllc.shootingstar.hr.dto.request;

public record LeaveQueryRequest(String institutionCode, String employeeId, String actorStaffCode, Integer year) {
}
