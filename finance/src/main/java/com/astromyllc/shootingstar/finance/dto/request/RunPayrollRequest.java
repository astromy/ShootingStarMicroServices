package com.astromyllc.shootingstar.finance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// institutionCode, createdBy and staff are set by the gateway (from the login and HR).
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RunPayrollRequest {
    private String institutionCode;
    private String academicYear;
    private String payPeriod;       // e.g. "September 2026"
    private String createdBy;
    private List<PayrollStaffRef> staff;
}
