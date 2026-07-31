package com.astromyllc.astroorb.dto.request;

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
    private String recordedBy;
    private String type;
}