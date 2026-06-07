package com.astromyllc.shootingstar.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalarySettingsResponse {
    private Long salarySettingsId;
    private String institutionCode;
    private Double ssnitEmployeeRate;
    private Double ssnitEmployerRate;
    private String incomeTaxBands;
    private List<SalaryItemResponse> defaultAllowances;
}