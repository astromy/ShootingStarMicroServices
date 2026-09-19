package com.astromyllc.shootingstar.setup.subscription;

import java.math.BigDecimal;

public record PricingResult(
        long population,
        SubscriptionPlan targetPlan,
        BigDecimal ratePerStudent,
        BigDecimal totalAmount
) {
}
