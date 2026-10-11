package com.astromyllc.shootingstar.hr.dto.response;

public record DetectResponse(int created, int updated, int removed, int unmatchedClockIns,
                             int unmatchedClockOuts, int overlong, String message) {
}
