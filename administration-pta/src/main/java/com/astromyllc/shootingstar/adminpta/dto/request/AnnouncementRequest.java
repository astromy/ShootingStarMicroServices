package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AnnouncementRequest {
    private String institutionCode;
    private String sentBy;
    private String title;
    private String message;

    // Empty/null = broadcast to every parent. Completely ignored (forced to
    // "everyone") when this request goes through sendEmergencyAlert — see
    // AnnouncementService.sendEmergencyAlert.
    private List<Long> targetClassIds;
}