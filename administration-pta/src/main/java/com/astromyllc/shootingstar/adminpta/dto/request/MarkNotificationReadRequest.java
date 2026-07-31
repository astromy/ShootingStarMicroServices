package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MarkNotificationReadRequest {
    private String institutionCode;
    private String recipientContact;
    private String notificationId;
    private String kind; // "ANNOUNCEMENT" | "EMERGENCY" | "VOICE"
}