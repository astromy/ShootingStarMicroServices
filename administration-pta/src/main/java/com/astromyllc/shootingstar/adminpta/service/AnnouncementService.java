package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.AnnouncementRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AnnouncementResponse;
import com.astromyllc.shootingstar.adminpta.model.Announcement;
import com.astromyllc.shootingstar.adminpta.repository.AnnouncementRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.AnnouncementServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.AnnouncementUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService implements AnnouncementServiceInterface {

    private final AnnouncementRepository announcementRepository;

    @Override
    public AnnouncementResponse sendAnnouncement(AnnouncementRequest request) {
        Announcement announcement = AnnouncementUtil.mapRequest_ToAnnouncement(request, "NORMAL");
        announcementRepository.save(announcement);
        log.info("Announcement sent for institution {} by {} (targeting {})",
                request.getInstitutionCode(), request.getSentBy(),
                (request.getTargetClassIds() == null || request.getTargetClassIds().isEmpty())
                        ? "all parents" : request.getTargetClassIds());
        return AnnouncementUtil.mapAnnouncement_ToAnnouncementResponse(announcement);
    }

    @Override
    public AnnouncementResponse sendEmergencyAlert(AnnouncementRequest request) {
        // Force school-wide regardless of what the client sent. An
        // "emergency alert" that could accidentally get scoped to one class
        // (client bug, stale state, whatever) defeats the entire point of
        // the feature — so this is enforced here, not just trusted from
        // the mobile app.
        AnnouncementRequest schoolWideRequest = AnnouncementRequest.builder()
                .institutionCode(request.getInstitutionCode())
                .sentBy(request.getSentBy())
                .title(request.getTitle())
                .message(request.getMessage())
                .targetClassIds(Collections.emptyList())
                .build();

        Announcement announcement = AnnouncementUtil.mapRequest_ToAnnouncement(schoolWideRequest, "EMERGENCY");
        announcementRepository.save(announcement);
        log.warn("EMERGENCY ALERT sent for institution {} by {}: {}",
                request.getInstitutionCode(), request.getSentBy(), request.getTitle());
        return AnnouncementUtil.mapAnnouncement_ToAnnouncementResponse(announcement);
    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsByInstitution(SingleStringRequest request) {
        List<Announcement> announcements = announcementRepository
                .findByInstitutionCodeOrderByTimestampDesc(request.getVal());
        return announcements.stream()
                .map(AnnouncementUtil::mapAnnouncement_ToAnnouncementResponse)
                .toList();
    }
}