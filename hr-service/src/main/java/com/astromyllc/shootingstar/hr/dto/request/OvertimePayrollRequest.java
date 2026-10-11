package com.astromyllc.shootingstar.hr.dto.request;

import java.time.LocalDate;
import java.util.List;

public record OvertimePayrollRequest(String institutionCode, LocalDate toDate, List<Allocation> allocations,
                                     List<String> payrollRefs) {
    public record Allocation(String payrollRef, List<String> recordIds) {
    }
}
