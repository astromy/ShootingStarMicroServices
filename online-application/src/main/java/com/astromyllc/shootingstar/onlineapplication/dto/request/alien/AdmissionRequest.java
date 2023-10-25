package com.astromyllc.shootingstar.onlineapplication.dto.request.alien;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdmissionRequest {
    private String institutionCode;
    private String applicationDate;
    public String applicationStatus;
}
