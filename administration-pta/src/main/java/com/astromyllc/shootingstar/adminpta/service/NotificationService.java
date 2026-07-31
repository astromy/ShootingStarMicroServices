package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AnnouncementResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.AnnouncementServiceInterface;
import com.astromyllc.shootingstar.adminpta.serviceInterface.NotificationServiceInterface;
import com.astromyllc.shootingstar.adminpta.serviceInterface.VoiceMessageServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationServiceInterface {

    private final AnnouncementServiceInterface announcementServiceInterface;
    private final VoiceMessageServiceInterface voiceMessageServiceInterface;

    @Override
    public List<NotificationResponse> getNotifications(SingleStringRequest institutionCode) {
        List<NotificationResponse> combined = new ArrayList<>();

        announcementServiceInterface.getAnnouncementsByInstitution(institutionCode)
                .forEach(a -> combined.add(mapAnnouncement(a)));

        voiceMessageServiceInterface.getVoiceMessagesByInstitution(institutionCode)
                .forEach(v -> combined.add(mapVoiceMessage(v)));

        combined.sort(Comparator.comparing(NotificationResponse::getTimestamp).reversed());
        return combined;
    }

    // ASSUMPTION: AnnouncementResponse has getters id/institutionCode/sentBy/
    // title/message/targetClassIds/type/timestamp — inferred from
    // AnnouncementService's usage, not from the DTO itself (haven't seen
    // that file). If a getter name here doesn't compile, match it to
    // whatever the real DTO actually calls it.
    private NotificationResponse mapAnnouncement(AnnouncementResponse a) {
        boolean isEmergency = "EMERGENCY".equalsIgnoreCase(a.getPriority());
        return NotificationResponse.builder()
                .id(a.getId())
                .kind(isEmergency ? "EMERGENCY" : "ANNOUNCEMENT")
                .institutionCode(a.getInstitutionCode())
                .sentBy(a.getSentBy())
                .title(a.getTitle())
                .message(a.getMessage())
                .targetClassIds(a.getTargetClassIds())
                .timestamp(a.getTimestamp())
                .build();
    }

    private NotificationResponse mapVoiceMessage(VoiceMessageResponse v) {
        return NotificationResponse.builder()
                .id(v.getId())
                .kind("VOICE")
                .institutionCode(v.getInstitutionCode())
                .sentBy(v.getSentBy())
                .title(v.getTitle())
                .targetClassIds(v.getTargetClassIds())
                .timestamp(v.getTimestamp())
                .voiceMessageId(v.getId())
                .mimeType(v.getMimeType())
                .durationSeconds(v.getDurationSeconds())
                .sizeBytes(v.getSizeBytes())
                .build();
    }
}