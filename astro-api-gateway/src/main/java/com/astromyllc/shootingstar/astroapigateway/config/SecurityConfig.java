package com.astromyllc.shootingstar.astroapigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SecurityConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter((request, next) -> {
                    System.out.println("Headers: " + request.headers());
                    return next.exchange(request);
                })
                .build();
    }
}