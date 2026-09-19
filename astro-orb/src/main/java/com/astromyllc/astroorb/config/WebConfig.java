package com.astromyllc.astroorb.config;

import com.astromyllc.astroorb.subscription.SubscriptionEnforcementInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// NOTE: the WebClient.Builder beans (webClientBuilder / directWebClientBuilder) and
// the ObjectMapper bean used to live here, but were moved out to WebClientConfig and
// JacksonConfig respectively, to break a circular dependency: this class depends on
// SubscriptionEnforcementInterceptor, which (directly, or via
// InstitutionSubscriptionService) depends on those beans. See WebClientConfig's and
// JacksonConfig's javadoc for the full explanation. If you're tempted to move a bean
// back into this class, check first whether SubscriptionEnforcementInterceptor or
// anything it depends on needs that bean - if so, it belongs in a separate
// no-dependency-on-WebConfig configuration class instead, same as these two.
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SubscriptionEnforcementInterceptor subscriptionEnforcementInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String sessionId = request.getSession(false) != null
                        ? request.getSession(false).getId()
                        : "NO_SESSION";

                try {
                    log.info("instance={}, sessionId={}, method={}, uri={}",
                            java.net.InetAddress.getLocalHost().getHostName(),
                            sessionId,
                            request.getMethod(),
                            request.getRequestURI());
                } catch (Exception e) {
                    log.info("instance=UNKNOWN, sessionId={}, method={}, uri={}",
                            sessionId,
                            request.getMethod(),
                            request.getRequestURI());
                }

                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                        Object handler, Exception ex) {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("X-Content-Type-Options", "nosniff");
            }
        });

        registry.addInterceptor(subscriptionEnforcementInterceptor);
    }
}
