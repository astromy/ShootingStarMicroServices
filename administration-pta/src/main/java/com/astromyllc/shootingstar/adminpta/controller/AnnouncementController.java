package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.request.AnnouncementRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AnnouncementResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.AnnouncementServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnnouncementController {

    private final AnnouncementServiceInterface announcementServiceInterface;

    @PostMapping("/api/administration-pta/sendAnnouncement")
    public ResponseEntity<AnnouncementResponse> sendAnnouncement(@RequestBody AnnouncementRequest request) {
        log.info("Sending announcement for institution {}", request.getInstitutionCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementServiceInterface.sendAnnouncement(request));
    }

    @PostMapping("/api/administration-pta/sendEmergencyAlert")
    public ResponseEntity<AnnouncementResponse> sendEmergencyAlert(@RequestBody AnnouncementRequest request) {
        log.warn("Sending EMERGENCY ALERT for institution {}", request.getInstitutionCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementServiceInterface.sendEmergencyAlert(request));
    }

    @PostMapping("/api/administration-pta/getAnnouncements")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(announcementServiceInterface.getAnnouncementsByInstitution(request));
    }
}