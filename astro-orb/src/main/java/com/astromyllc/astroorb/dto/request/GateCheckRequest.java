package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GateCheckRequest {
    private String studentId;
    private String institutionCode;
    private String recordedBy; // staffCode of the guard performing the scan
    private String type;       // "IN" or "OUT"
}
