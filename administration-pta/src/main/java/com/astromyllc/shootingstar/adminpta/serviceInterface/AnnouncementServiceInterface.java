package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.AnnouncementRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementServiceInterface {
    AnnouncementResponse sendAnnouncement(AnnouncementRequest request);

    // Always broadcasts to every parent at the institution — targetClassIds
    // on the incoming request is ignored/overridden even if the client sent
    // one, since an emergency alert is defined as school-wide by nature.
    AnnouncementResponse sendEmergencyAlert(AnnouncementRequest request);

    List<AnnouncementResponse> getAnnouncementsByInstitution(SingleStringRequest request);
}