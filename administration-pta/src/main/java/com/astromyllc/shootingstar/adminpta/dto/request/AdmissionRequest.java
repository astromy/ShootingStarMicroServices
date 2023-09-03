package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AdmissionRequest {
    private String institutionCode;
    private String applicationDate;
}
