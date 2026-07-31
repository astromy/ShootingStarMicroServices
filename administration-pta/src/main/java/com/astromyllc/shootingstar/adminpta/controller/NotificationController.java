package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.NotificationResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.NotificationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationServiceInterface notificationServiceInterface;

    @PostMapping("/api/administration-pta/getNotifications")
    public List<NotificationResponse> getNotifications(@RequestBody SingleStringRequest institutionCode) {
        return notificationServiceInterface.getNotifications(institutionCode);
    }
}