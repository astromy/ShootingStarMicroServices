package com.astromyllc.shootingstar.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClockEventResponse {
    private String staffCode;
    private String institutionCode;
    private String type;
    private double latitude;
    private double longitude;
    private Instant timestamp;
}