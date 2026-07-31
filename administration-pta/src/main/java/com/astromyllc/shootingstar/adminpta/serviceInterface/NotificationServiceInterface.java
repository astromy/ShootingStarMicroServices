package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationServiceInterface {
    // Merges Announcements (NORMAL + EMERGENCY) and VoiceMessages for the
    // institution into one feed, newest first.
    List<NotificationResponse> getNotifications(SingleStringRequest institutionCode);
}