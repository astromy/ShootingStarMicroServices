package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AnnouncementResponse {
    private String id;
    private String institutionCode;
    private String title;
    private String message;
    private String sentBy;
    private String priority;
    private List<Long> targetClassIds;
    private Instant timestamp;
}