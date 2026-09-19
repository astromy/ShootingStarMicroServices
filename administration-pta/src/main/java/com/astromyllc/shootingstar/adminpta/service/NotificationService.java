package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.MarkNotificationReadRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentEventNotificationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;
import com.astromyllc.shootingstar.adminpta.model.Notification;
import com.astromyllc.shootingstar.adminpta.repository.AnnouncementRepository;
import com.astromyllc.shootingstar.adminpta.repository.NotificationRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.NotificationServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationServiceInterface {

    private final NotificationRepository notificationRepository;
    private final AnnouncementRepository announcementRepository;

    @Override
    public Map<String, Object> notifyParentsOfStudentEvent(StudentEventNotificationRequest request) {
        Notification notification = NotificationUtil.mapRequest_ToNotification(request);
        notification = notificationRepository.save(notification);
        log.info("Recorded {} notification for student {} at institution {}",
                notification.getKind(), request.getStudentId(), request.getInstitutionCode());
        return Map.of("status", "ok", "id", notification.getId().toHexString());
    }

    // Merges the institution's Announcements (broadcast) with its targeted
    // Notifications (e.g. CLINIC) into one time-sorted feed — matching what
    // the mobile app's single Notifications screen expects.
    @Override
    public List<NotificationResponse> getNotifications(String institutionCode) {
        List<NotificationResponse> announcements = announcementRepository
                .findByInstitutionCodeOrderByTimestampDesc(institutionCode).stream()
                .map(NotificationUtil::mapAnnouncement_ToNotificationResponse)
                .toList();

        List<NotificationResponse> studentEvents = notificationRepository
                .findByInstitutionCodeOrderByTimestampDesc(institutionCode).stream()
                .map(NotificationUtil::mapNotification_ToNotificationResponse)
                .toList();

        return Stream.concat(announcements.stream(), studentEvents.stream())
                .sorted(Comparator.comparing(NotificationResponse::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    // Only the new per-student Notification collection tracks a "read"
    // flag today — Announcement has no per-recipient read state modeled
    // (it's institution-wide, not addressed to one parent), so an
    // ANNOUNCEMENT/EMERGENCY id here is a no-op rather than an error.
    @Override
    public void markNotificationRead(MarkNotificationReadRequest request) {
        if (!"CLINIC".equalsIgnoreCase(request.getKind())) {
            log.debug("markNotificationRead: no per-recipient read state for kind {} — ignoring", request.getKind());
            return;
        }
        try {
            notificationRepository.findByIdAndInstitutionCode(
                    new ObjectId(request.getNotificationId()), request.getInstitutionCode())
                    .ifPresent(notification -> {
                        notification.setRead(true);
                        notificationRepository.save(notification);
                    });
        } catch (IllegalArgumentException ex) {
            log.warn("markNotificationRead: invalid notification id {}", request.getNotificationId());
        }
    }
}
