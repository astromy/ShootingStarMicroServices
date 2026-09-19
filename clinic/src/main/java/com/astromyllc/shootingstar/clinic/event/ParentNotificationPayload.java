package com.astromyllc.shootingstar.clinic.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload posted to administration-pta's notifyParentsOfStudentEvent
 * endpoint. Mirrors the role LedgerPostRequest plays for stores-inventory's
 * FinanceLedgerClient — an outbound event shape, not a local model.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParentNotificationPayload {
    private String institutionCode;
    private String studentId;
    private String category;       // "HEALTH_VISIT"
    private String sourceModule;   // "clinic"
    private String title;
    private String message;
}