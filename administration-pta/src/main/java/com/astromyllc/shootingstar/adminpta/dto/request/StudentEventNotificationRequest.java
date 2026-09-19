package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirrors clinic's ParentNotificationPayload field-for-field — this is what
 * lands in the body of POST /api/administration-pta/notifyParentsOfStudentEvent.
 * Any other module wanting to notify a specific student's parent (not just
 * clinic) can reuse this same shape.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentEventNotificationRequest {
    private String institutionCode;
    private String studentId;
    private String category;       // e.g. "HEALTH_VISIT"
    private String sourceModule;   // e.g. "clinic"
    private String title;
    private String message;
}
