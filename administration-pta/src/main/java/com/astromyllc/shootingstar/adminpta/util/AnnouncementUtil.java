package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.AnnouncementRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AnnouncementResponse;
import com.astromyllc.shootingstar.adminpta.model.Announcement;

import java.time.Instant;
import java.util.Collections;

public class AnnouncementUtil {

    public static Announcement mapRequest_ToAnnouncement(AnnouncementRequest request, String priority) {
        return Announcement.builder()
                .institutionCode(request.getInstitutionCode())
                .title(request.getTitle())
                .message(request.getMessage())
                .sentBy(request.getSentBy())
                .priority(priority)
                .targetClassIds(request.getTargetClassIds() == null ? Collections.emptyList() : request.getTargetClassIds())
                .timestamp(Instant.now())
                .build();
    }

    public static AnnouncementResponse mapAnnouncement_ToAnnouncementResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId() != null ? announcement.getId().toHexString() : null)
                .institutionCode(announcement.getInstitutionCode())
                .title(announcement.getTitle())
                .message(announcement.getMessage())
                .sentBy(announcement.getSentBy())
                .priority(announcement.getPriority())
                .targetClassIds(announcement.getTargetClassIds())
                .timestamp(announcement.getTimestamp())
                .build();
    }
}