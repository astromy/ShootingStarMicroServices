package com.astromyllc.shootingstar.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryItemResponse {
    private Long salaryItemId;
    private String itemName;
    private String itemType;
    private Double amount;
    private Boolean isPercentage;
    private Double percentageRate;
}