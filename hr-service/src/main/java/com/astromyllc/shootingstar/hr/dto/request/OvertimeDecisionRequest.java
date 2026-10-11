package com.astromyllc.shootingstar.hr.dto.request;

import java.util.List;

public record OvertimeDecisionRequest(String institutionCode, String decidedBy, List<Item> items,
                                      List<String> ids, String note) {
    /** approvedMinutes may be lower than what was detected, never higher. */
    public record Item(String id, Integer approvedMinutes) {
    }
}
