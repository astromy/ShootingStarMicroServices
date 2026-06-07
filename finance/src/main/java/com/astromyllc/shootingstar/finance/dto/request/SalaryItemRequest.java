package com.astromyllc.shootingstar.finance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryItemRequest {
    private Long salaryItemId;
    private String itemName;
    private String itemType;
    private Double amount;
    private Boolean isPercentage;
    private Double percentageRate;
}