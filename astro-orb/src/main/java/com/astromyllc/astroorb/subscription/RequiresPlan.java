package com.astromyllc.astroorb.subscription;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller class or method as requiring at least the given
 * {@link SubscriptionPlan} tier. Enforced by {@link SubscriptionEnforcementInterceptor}.
 * <p>
 * Apply at class level to gate an entire module (e.g. {@code AcademicsController}),
 * or at method level to gate a single endpoint within an otherwise-unrestricted
 * controller (e.g. the payroll endpoints inside {@code FinanceController}).
 * A method-level annotation overrides a class-level one on the same controller.
 * <p>
 * Endpoints with no {@code @RequiresPlan} annotation are available on every plan.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequiresPlan {
    SubscriptionPlan value();
}
