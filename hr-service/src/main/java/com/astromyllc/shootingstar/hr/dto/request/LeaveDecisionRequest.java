package com.astromyllc.shootingstar.hr.dto.request;

public record LeaveDecisionRequest(String institutionCode, String leaveId, String status, String remarks,
                                   String actor, String actorStaffCode) {
}
