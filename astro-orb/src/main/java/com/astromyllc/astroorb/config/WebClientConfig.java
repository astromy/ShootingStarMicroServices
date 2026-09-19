package com.astromyllc.astroorb.config;

import com.astromyllc.astroorb.utils.TokenHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Split out of WebConfig on purpose: WebConfig injects
 * {@code SubscriptionEnforcementInterceptor}, which (via
 * {@code InstitutionSubscriptionService}) depends on the {@code webClientBuilder}/
 * {@code directWebClientBuilder} beans. If those beans were still defined as
 * {@code @Bean} methods on WebConfig itself, Spring would need WebConfig fully
 * constructed before it could produce them - but constructing WebConfig requires
 * the interceptor first, which requires these beans first. That's the circular
 * reference. Keeping them in a separate configuration class with no dependency
 * back on WebConfig breaks the cycle.
 */
@Configuration
public class WebClientConfig {

    @Bean
    //@LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(tokenForwardingFilter())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024));
    }

    @Bean
    public WebClient.Builder directWebClientBuilder() {
        System.out.println("🚀 Creating directWebClientBuilder bean!");
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024));
    }

    private ExchangeFilterFunction tokenForwardingFilter() {
        return (request, next) -> {
            // Get the token from TokenHolder (captured by TokenCaptureFilter)
            String token = TokenHolder.getToken();

            String url = request.url().toString();
            System.out.println("🔵 WebClient request to: " + url);

            if (token != null && !token.isEmpty()) {
                System.out.println("✅ Forwarding token to: " + url);
                System.out.println("   Token preview: " + token.substring(0, Math.min(20, token.length())) + "...");

                ClientRequest filteredRequest = ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build();

                return next.exchange(filteredRequest)
                        .doOnNext(response ->
                                System.out.println("📥 Response from " + url + ": " + response.statusCode()));
            } else {
                System.out.println("❌ NO TOKEN to forward for: " + url);
            }

            return next.exchange(request);
        };
    }
}
