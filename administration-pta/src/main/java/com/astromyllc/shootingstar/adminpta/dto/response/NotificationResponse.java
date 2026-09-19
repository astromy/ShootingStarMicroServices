package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Unified shape for the mobile app's single "Notifications" feed — matches
 * what apiService.js's transformNotificationsResponse on the mobile app
 * already expects (kind/institutionCode/title/message/sentBy/timestamp/read).
 * Backed by two different sources merged together: existing Announcement
 * documents (kind ANNOUNCEMENT/EMERGENCY) and the new per-student
 * Notification documents (kind CLINIC today; open to other modules later).
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationResponse {
    private String id;
    private String kind;            // ANNOUNCEMENT | EMERGENCY | CLINIC
    private String institutionCode;
    private String studentId;       // null for institution-wide announcements
    private String title;
    private String message;
    private String sentBy;
    private Instant timestamp;
    private boolean read;
}
