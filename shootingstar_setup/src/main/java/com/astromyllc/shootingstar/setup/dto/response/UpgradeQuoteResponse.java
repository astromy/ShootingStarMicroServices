package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpgradeQuoteResponse {
    private String institutionCode;
    private String currentPlan;
    private String targetPlan;
    private Long population;
    private BigDecimal ratePerStudent;
    private BigDecimal totalAmount;
    private String currency;
    /** Paystack expects amounts in the currency's smallest subunit (pesewas for GHS). */
    private Long amountInSubunit;
}
