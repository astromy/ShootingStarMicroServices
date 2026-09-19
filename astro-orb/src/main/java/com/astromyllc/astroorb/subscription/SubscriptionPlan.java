package com.astromyllc.astroorb.subscription;

import lombok.extern.slf4j.Slf4j;

/**
 * The three subscription tiers offered on the pricing page (Starter / Growth / Enterprise).
 * <p>
 * Tiers are ordered by {@link #level}: a higher level includes everything the lower
 * levels include ("Everything in Starter, plus..."), matching how the plans are
 * marketed. Enforcement code should compare levels rather than plan identity.
 */
@Slf4j
public enum SubscriptionPlan {

    STARTER(0),
    GROWTH(1),
    ENTERPRISE(2);

    private final int level;

    SubscriptionPlan(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean atLeast(SubscriptionPlan required) {
        return this.level >= required.level;
    }

    /**
     * Parses the free-text {@code subscription} value stored on the institution
     * (e.g. "Growth plan") into a {@link SubscriptionPlan}.
     * <p>
     * Tolerant of the legacy tier names used before the pricing page was updated
     * (Free/Basic -&gt; Starter, Standard -&gt; Growth, Professional -&gt; Enterprise),
     * so institutions that subscribed under the old naming aren't accidentally
     * downgraded.
     * <p>
     * Unrecognized or missing values fall back to {@link #STARTER}, the lowest
     * tier, so an institution is never granted access it hasn't paid for just
     * because of a typo or an unexpected value from the backend.
     */
    public static SubscriptionPlan fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return STARTER;
        }

        String normalized = label.trim().toLowerCase();

        if (normalized.contains("enterprise") || normalized.contains("professional")) {
            return ENTERPRISE;
        }
        if (normalized.contains("growth") || normalized.contains("standard")) {
            return GROWTH;
        }
        if (normalized.contains("starter") || normalized.contains("basic") || normalized.contains("free")) {
            return STARTER;
        }

        log.warn("Unrecognized subscription label '{}', defaulting to STARTER", label);
        return STARTER;
    }
}
