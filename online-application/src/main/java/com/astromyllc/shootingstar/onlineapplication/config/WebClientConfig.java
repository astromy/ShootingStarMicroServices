package com.astromyllc.shootingstar.onlineapplication.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();

    }

    @Bean
    public WebClient plainWebClient() {
        return WebClient.builder().build(); // ✅ no load balancing
    }
}
