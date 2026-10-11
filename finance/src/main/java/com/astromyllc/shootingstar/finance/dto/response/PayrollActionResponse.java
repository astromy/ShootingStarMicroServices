package com.astromyllc.shootingstar.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a bulk payroll action: the runs created or changed, and anything
 * left alone with the reason (already run, wrong status, no profile, ...).
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PayrollActionResponse {
    private List<SalaryResponse> updated = new ArrayList<>();
    private List<Skipped> skipped = new ArrayList<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Skipped {
        private Long salaryId;
        private String staffId;
        private String staffName;
        private String reason;
    }
}
