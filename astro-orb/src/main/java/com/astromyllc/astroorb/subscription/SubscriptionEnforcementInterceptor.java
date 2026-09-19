package com.astromyllc.astroorb.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Blocks requests to endpoints annotated {@link RequiresPlan} when the logged-in
 * user's institution isn't subscribed to a high enough tier.
 * <p>
 * Handles both ways this app authenticates:
 * <ul>
 *     <li>Browser/OAuth2 session login ({@link OAuth2AuthenticationToken}) -
 *     institution code from the {@code institution_group} principal attribute,
 *     plan cached in the HTTP session.</li>
 *     <li>Mobile bearer-JWT login ({@link JwtAuthenticationToken}, see
 *     {@code SecurityConfig}'s {@code mobileSecurityFilterChain}) - institution
 *     code from the JWT's {@code groups[0]} claim, confirmed against a real
 *     token from the Pulse mobile app (see its {@code utils/jwt.js}, which reads
 *     the same claim client-side for its own role checks). Not session-cached,
 *     since mobile API calls are stateless.</li>
 * </ul>
 * <p>
 * Requests where the plan can't be resolved at all (neither auth type matches,
 * or the relevant claim/attribute is missing/unparsable) are allowed through
 * rather than blocked, since this interceptor's job is feature gating, not
 * authentication — {@code SecurityConfig} is what actually guards access to
 * authenticated routes.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionEnforcementInterceptor implements HandlerInterceptor {

    private final InstitutionSubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresPlan requiresPlan = handlerMethod.getMethodAnnotation(RequiresPlan.class);
        if (requiresPlan == null) {
            requiresPlan = handlerMethod.getBeanType().getAnnotation(RequiresPlan.class);
        }
        if (requiresPlan == null) {
            return true; // endpoint isn't gated by any plan
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SubscriptionPlan currentPlan;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User principal = oauthToken.getPrincipal();
            HttpSession session = request.getSession(true);
            currentPlan = subscriptionService.resolvePlan(session, principal);
        } else if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();
            currentPlan = subscriptionService.resolvePlanForJwt(jwt);
        } else {
            // Neither auth type - not enforced here, see class javadoc.
            return true;
        }

        SubscriptionPlan requiredPlan = requiresPlan.value();

        if (currentPlan.atLeast(requiredPlan)) {
            return true;
        }

        log.info("Blocking {} {} - institution plan {} does not meet required plan {}",
                request.getMethod(), request.getRequestURI(), currentPlan, requiredPlan);

        respondWithUpgradeRequired(request, response, currentPlan, requiredPlan);
        return false;
    }

    private void respondWithUpgradeRequired(HttpServletRequest request, HttpServletResponse response,
                                             SubscriptionPlan currentPlan, SubscriptionPlan requiredPlan) throws Exception {
        if (wantsJson(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                    "error", "UPGRADE_REQUIRED",
                    "message", "This feature requires the " + requiredPlan.name() + " plan or higher.",
                    "currentPlan", currentPlan.name(),
                    "requiredPlan", requiredPlan.name()
            )));
            return;
        }

        response.sendRedirect("/upgrade-required?required=" + requiredPlan.name() + "&current=" + currentPlan.name());
    }

    private boolean wantsJson(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        return uri.contains("/api/")
                || (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE))
                || "XMLHttpRequest".equals(requestedWith);
    }
}
