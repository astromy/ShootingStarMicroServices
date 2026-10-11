package com.astromyllc.shootingstar.finance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// institutionCode and actor are set by the gateway from the login.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PayrollActionRequest {
    private String institutionCode;
    private List<Long> salaryIds;
    private String actor;
    private String externalReference;   // optional, for mark-paid
}
