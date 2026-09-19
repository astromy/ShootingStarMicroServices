package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.StudentEventNotificationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;
import com.astromyllc.shootingstar.adminpta.model.Announcement;
import com.astromyllc.shootingstar.adminpta.model.Notification;

import java.time.Instant;

public class NotificationUtil {

    // "HEALTH_VISIT" -> "CLINIC", etc. Falls back to the source module name
    // (upper-cased) for any category this hasn't been taught about yet,
    // rather than silently mislabeling it as CLINIC.
    public static String mapCategory_ToKind(String category, String sourceModule) {
        if ("HEALTH_VISIT".equalsIgnoreCase(category)) {
            return "CLINIC";
        }
        return sourceModule != null ? sourceModule.toUpperCase() : "GENERAL";
    }

    public static Notification mapRequest_ToNotification(StudentEventNotificationRequest request) {
        return Notification.builder()
                .institutionCode(request.getInstitutionCode())
                .studentId(request.getStudentId())
                .kind(mapCategory_ToKind(request.getCategory(), request.getSourceModule()))
                .category(request.getCategory())
                .sourceModule(request.getSourceModule())
                .title(request.getTitle())
                .message(request.getMessage())
                .sentBy(request.getSourceModule())
                .timestamp(Instant.now())
                .read(false)
                .build();
    }

    public static NotificationResponse mapNotification_ToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId() != null ? notification.getId().toHexString() : null)
                .kind(notification.getKind())
                .institutionCode(notification.getInstitutionCode())
                .studentId(notification.getStudentId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .sentBy(notification.getSentBy())
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .build();
    }

    // Folds an existing Announcement into the same unified feed shape so
    // the mobile app's single "Notifications" screen shows both broadcast
    // announcements and per-student events in one merged, time-sorted list.
    public static NotificationResponse mapAnnouncement_ToNotificationResponse(Announcement announcement) {
        // Announcement.priority is "NORMAL" | "EMERGENCY" (see AnnouncementService)
        // but the mobile app's kind vocabulary is "ANNOUNCEMENT" | "EMERGENCY".
        String kind = "EMERGENCY".equalsIgnoreCase(announcement.getPriority()) ? "EMERGENCY" : "ANNOUNCEMENT";
        return NotificationResponse.builder()
                .id(announcement.getId() != null ? announcement.getId().toHexString() : null)
                .kind(kind)
                .institutionCode(announcement.getInstitutionCode())
                .studentId(null)
                .title(announcement.getTitle())
                .message(announcement.getMessage())
                .sentBy(announcement.getSentBy())
                .timestamp(announcement.getTimestamp())
                .read(false)
                .build();
    }
}
