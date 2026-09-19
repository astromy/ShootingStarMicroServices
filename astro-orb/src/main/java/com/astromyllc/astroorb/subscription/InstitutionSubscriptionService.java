package com.astromyllc.astroorb.subscription;

import com.astromyllc.astroorb.dto.request.SingleStringRequest;
import com.astromyllc.astroorb.dto.response.SkimpInstitutionResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

/**
 * Resolves the subscription plan of the currently logged-in user's institution,
 * for use by {@link SubscriptionEnforcementInterceptor}.
 * <p>
 * Two entry points, because the browser and mobile app authenticate
 * differently and carry the institution code under different claims:
 * <ul>
 *     <li>{@link #resolvePlan(HttpSession, OAuth2User)} - browser/OAuth2 session
 *     login, institution code comes from the {@code institution_group} principal
 *     attribute, result cached in the HTTP session for {@link #CACHE_TTL_SECONDS}.</li>
 *     <li>{@link #resolvePlanForJwt(Jwt)} - mobile bearer-JWT login, institution
 *     code comes from {@code groups[0]} (confirmed against a real mobile token -
 *     see the Pulse app's {@code utils/jwt.js}, which reads the same claim
 *     client-side). No session exists for stateless API calls, so this isn't
 *     cached here; each call is a fresh lookup.</li>
 * </ul>
 * Both funnel into {@link #resolvePlanForInstitutionCode}, so there's one
 * implementation of "how do we ask the backend for this institution's plan."
 * <p>
 * Fail-open on backend errors: if the plan can't be determined (backend down,
 * unexpected response, etc.) this returns {@link SubscriptionPlan#ENTERPRISE} so a
 * backend outage degrades to "enforcement temporarily disabled" rather than
 * locking every institution out of every feature. This mirrors the existing
 * fail-open behaviour in {@code UserController#createDefaultResponse} (which
 * assumes "Active" status if the backend call fails). If you'd rather fail
 * closed during an outage, change {@link #FALLBACK_PLAN} to {@code STARTER}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionSubscriptionService {

    private static final String SESSION_PLAN_ATTR = "orb.subscriptionPlan";
    private static final String SESSION_PLAN_CACHED_AT_ATTR = "orb.subscriptionPlanCachedAt";
    private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
    private static final SubscriptionPlan FALLBACK_PLAN = SubscriptionPlan.ENTERPRISE;

    @Qualifier("webClientBuilder")
    private final WebClient.Builder webClientBuilder;

    @Qualifier("directWebClientBuilder")
    private final WebClient.Builder directWebClientBuilder;

    @Value("${gateway.host}")
    private String backendserve;

    /**
     * Resolves the subscription plan for the given session/principal, using the
     * session cache when it's still fresh.
     */
    public SubscriptionPlan resolvePlan(HttpSession session, OAuth2User principal) {
        if (session != null) {
            Object cachedPlan = session.getAttribute(SESSION_PLAN_ATTR);
            Object cachedAt = session.getAttribute(SESSION_PLAN_CACHED_AT_ATTR);
            if (cachedPlan instanceof String && cachedAt instanceof Long) {
                boolean fresh = Instant.now().getEpochSecond() - (Long) cachedAt < CACHE_TTL_SECONDS;
                if (fresh) {
                    try {
                        return SubscriptionPlan.valueOf((String) cachedPlan);
                    } catch (IllegalArgumentException ignored) {
                        // fall through and re-resolve
                    }
                }
            }
        }

        String institutionCode = extractInstitutionCode(principal);
        SubscriptionPlan resolved = institutionCode == null
                ? warnAndFallback("Could not resolve institution code from OAuth2 principal")
                : resolvePlanForInstitutionCode(institutionCode);

        if (session != null) {
            session.setAttribute(SESSION_PLAN_ATTR, resolved.name());
            session.setAttribute(SESSION_PLAN_CACHED_AT_ATTR, Instant.now().getEpochSecond());
        }

        return resolved;
    }

    /**
     * Resolves the subscription plan for a mobile bearer-JWT request. No
     * session-based caching here - each call is a fresh backend lookup, since
     * there's no reliable session to cache against for stateless API calls.
     */
    public SubscriptionPlan resolvePlanForJwt(Jwt jwt) {
        String institutionCode = extractInstitutionCodeFromJwt(jwt);
        if (institutionCode == null) {
            return warnAndFallback("Could not resolve institution code from JWT 'groups' claim");
        }
        return resolvePlanForInstitutionCode(institutionCode);
    }

    /** Forces the next {@link #resolvePlan} call for this session to hit the backend again. */
    public void invalidate(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_PLAN_ATTR);
            session.removeAttribute(SESSION_PLAN_CACHED_AT_ATTR);
        }
    }

    public SubscriptionPlan resolvePlanForInstitutionCode(String institutionCode) {
        try {
            SingleStringRequest request = new SingleStringRequest();
            request.setVal(institutionCode);

            WebClient.Builder builder = backendserve.contains("localhost") ? directWebClientBuilder : webClientBuilder;

            SkimpInstitutionResponse response = builder
                    .baseUrl(backendserve)
                    .build()
                    .post()
                    .uri("/api/setup/getInstitutionStatus")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(SkimpInstitutionResponse.class)
                    .block();

            if (response == null) {
                return warnAndFallback("Empty institution status response for '" + institutionCode + "'");
            }

            return SubscriptionPlan.fromLabel(response.getSubscription());
        } catch (Exception e) {
            log.error("Failed to resolve subscription plan for '{}'; defaulting to {}", institutionCode, FALLBACK_PLAN, e);
            return FALLBACK_PLAN;
        }
    }

    private SubscriptionPlan warnAndFallback(String reason) {
        log.warn("{}; defaulting plan to {}", reason, FALLBACK_PLAN);
        return FALLBACK_PLAN;
    }

    private String extractInstitutionCode(OAuth2User principal) {
        if (principal == null) {
            return null;
        }
        Object institutionObj = principal.getAttribute("institution_group");
        if (institutionObj == null) {
            return null;
        }
        try {
            return institutionObj.toString().split(",")[0].split("/")[1].replace("]", "").trim();
        } catch (Exception e) {
            log.warn("Could not parse institution_group attribute '{}'", institutionObj);
            return null;
        }
    }

    /**
     * Mirrors the Pulse mobile app's utils/jwt.js#getInstitutionCodeFromToken:
     * the institution code is the first entry in the JWT's "groups" claim.
     */
    @SuppressWarnings("unchecked")
    private String extractInstitutionCodeFromJwt(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        Object groupsObj = jwt.getClaim("groups");
        if (!(groupsObj instanceof List<?> groups) || groups.isEmpty()) {
            return null;
        }
        Object first = groups.get(0);
        return first != null ? first.toString() : null;
    }
}

