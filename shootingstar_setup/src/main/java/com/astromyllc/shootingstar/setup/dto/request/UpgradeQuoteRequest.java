package com.astromyllc.shootingstar.setup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpgradeQuoteRequest {
    private String institutionCode;
    /** "GROWTH" or "ENTERPRISE" - parsed tolerantly, see SubscriptionPlan.fromLabel. */
    private String targetPlan;
}
