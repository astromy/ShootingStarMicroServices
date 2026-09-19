package com.astromyllc.shootingstar.setup.subscription;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Computes what an institution should pay to upgrade to Growth or Enterprise,
 * based on student population.
 * <p>
 * Business requirement: the Growth per-student rate is linearly interpolated
 * across the 45-1000 student range - GHS 60/student at 45 students down to
 * GHS 20/student at 1000 students.
 * <p>
 * Below 45 or above 1000 students: the rate is capped at whichever boundary
 * it's closest to (GHS 60/student for anything under 45, GHS 20/student for
 * anything over 1000) rather than rejected or extrapolated further - the
 * institution is still charged for its actual population, just at the
 * capped rate.
 * <p>
 * Enterprise pricing reuses the same curve, with both anchor rates increased
 * 30% (GHS 78/student capped low, GHS 26/student capped high), per the
 * stated business requirement.
 */
@Component
public class SubscriptionPricingCalculator {

    public static final int MIN_STUDENTS = 45;
    public static final int MAX_STUDENTS = 1000;
    private static final int ANCHOR_LOW_STUDENTS = MIN_STUDENTS;
    private static final int ANCHOR_HIGH_STUDENTS = MAX_STUDENTS;

    private static final BigDecimal GROWTH_RATE_AT_LOW = BigDecimal.valueOf(60);
    private static final BigDecimal GROWTH_RATE_AT_HIGH = BigDecimal.valueOf(20);
    private static final BigDecimal ENTERPRISE_MULTIPLIER = BigDecimal.valueOf(1.30);

    public PricingResult calculate(long population, SubscriptionPlan targetPlan) {
        if (targetPlan != SubscriptionPlan.GROWTH && targetPlan != SubscriptionPlan.ENTERPRISE) {
            throw new IllegalArgumentException(
                    "Only GROWTH or ENTERPRISE can be purchased through this flow; " + targetPlan + " is not a paid upgrade target.");
        }
        if (population <= 0) {
            throw new IllegalArgumentException("Institution population must be a positive number of students.");
        }

        BigDecimal growthRate = growthRatePerStudent(population);
        BigDecimal rate = targetPlan == SubscriptionPlan.ENTERPRISE
                ? growthRate.multiply(ENTERPRISE_MULTIPLIER)
                : growthRate;
        rate = rate.setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = rate.multiply(BigDecimal.valueOf(population)).setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(population, targetPlan, rate, total);
    }

    private BigDecimal growthRatePerStudent(long population) {
        if (population <= ANCHOR_LOW_STUDENTS) {
            return GROWTH_RATE_AT_LOW;
        }
        if (population >= ANCHOR_HIGH_STUDENTS) {
            return GROWTH_RATE_AT_HIGH;
        }
        BigDecimal slope = GROWTH_RATE_AT_HIGH.subtract(GROWTH_RATE_AT_LOW)
                .divide(BigDecimal.valueOf(ANCHOR_HIGH_STUDENTS - ANCHOR_LOW_STUDENTS), 10, RoundingMode.HALF_UP);
        BigDecimal delta = BigDecimal.valueOf(population - ANCHOR_LOW_STUDENTS);
        return GROWTH_RATE_AT_LOW.add(slope.multiply(delta));
    }
}
