package com.astromyllc.shootingstar.hr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffClockInRequest {
    private String staffId;
    private String institutionCode;
    private double latitude;
    private double longitude;
    private String type;           // "IN" or "OUT"
}