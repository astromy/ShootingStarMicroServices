package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.request.MarkNotificationReadRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentEventNotificationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.NotificationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationServiceInterface notificationServiceInterface;

    // Called by other modules (clinic today, via ParentNotificationClient)
    // to notify a specific student's parent of something that happened to
    // their child. Not parent/mobile-facing directly.
    @PostMapping("/api/administration-pta/notifyParentsOfStudentEvent")
    public ResponseEntity<Map<String, Object>> notifyParentsOfStudentEvent(@RequestBody StudentEventNotificationRequest request) {
        log.info("notifyParentsOfStudentEvent: {} for student {} at institution {}",
                request.getCategory(), request.getStudentId(), request.getInstitutionCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationServiceInterface.notifyParentsOfStudentEvent(request));
    }

    // Backs "api/mobile/getNotifications" on astro-orb's gateway.
    @PostMapping("/api/administration-pta/getNotifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(notificationServiceInterface.getNotifications(request.getVal()));
    }

    // Backs "api/mobile/markNotificationRead" on astro-orb's gateway.
    @PostMapping("/api/administration-pta/markNotificationRead")
    public ResponseEntity<Void> markNotificationRead(@RequestBody MarkNotificationReadRequest request) {
        notificationServiceInterface.markNotificationRead(request);
        return ResponseEntity.ok().build();
    }
}
