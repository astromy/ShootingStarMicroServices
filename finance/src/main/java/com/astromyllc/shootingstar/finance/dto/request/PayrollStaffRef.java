package com.astromyllc.shootingstar.finance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// A staff member as listed in HR - sent by the gateway with each payroll run.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PayrollStaffRef {
    private String staffId;
    private String staffName;
    private String designation;
}
