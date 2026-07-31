package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusBoardingRequest {
    private String studentId;
    private String institutionCode;
    private Long busId;
    private String busName;
    private Long routeId;
    private String routeName;
    private String recordedBy; // staffCode of the conductor/driver performing the scan
    private String type;       // "BOARD" or "ALIGHT"
}