package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.MarkNotificationReadRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentEventNotificationRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;

import java.util.List;
import java.util.Map;

public interface NotificationServiceInterface {

    Map<String, Object> notifyParentsOfStudentEvent(StudentEventNotificationRequest request);

    List<NotificationResponse> getNotifications(String institutionCode);

    void markNotificationRead(MarkNotificationReadRequest request);
}
