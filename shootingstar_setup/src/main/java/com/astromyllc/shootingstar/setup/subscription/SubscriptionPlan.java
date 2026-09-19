package com.astromyllc.shootingstar.setup.subscription;

import lombok.extern.slf4j.Slf4j;

/**
 * Mirrors the tier model used on the astro-orb frontend
 * (com.astromyllc.astroorb.subscription.SubscriptionPlan) - kept as a separate
 * copy rather than a shared dependency since these are independently deployed
 * microservices, but the parsing rules are deliberately identical so a given
 * institution.subscription string is interpreted the same way by both.
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

    public boolean isHigherThan(SubscriptionPlan other) {
        return this.level > other.level;
    }

    /** The literal string stored on Institution.subscription for this plan. */
    public String storageLabel() {
        return switch (this) {
            case STARTER -> "Starter plan";
            case GROWTH -> "Growth plan";
            case ENTERPRISE -> "Enterprise plan";
        };
    }

    /**
     * Tolerant parsing of the free-text subscription field, including the legacy
     * tier names used before the pricing page was renamed (Free/Basic -> Starter,
     * Standard -> Growth, Professional -> Enterprise). Unrecognized or missing
     * values fall back to STARTER, the lowest tier.
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
