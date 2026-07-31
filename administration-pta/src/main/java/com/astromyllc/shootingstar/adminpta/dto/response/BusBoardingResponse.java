package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusBoardingResponse {
    private String studentId;
    private String institutionCode;
    private Long busId;
    private String busName;
    private String recordedBy;
    private String type;
    private Instant timestamp;
    private boolean routeMismatch;
    private String expectedRouteName;
}