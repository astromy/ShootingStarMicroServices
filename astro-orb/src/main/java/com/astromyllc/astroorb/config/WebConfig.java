package com.astromyllc.astroorb.config;

import com.astromyllc.astroorb.utils.TokenHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

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
    }
}