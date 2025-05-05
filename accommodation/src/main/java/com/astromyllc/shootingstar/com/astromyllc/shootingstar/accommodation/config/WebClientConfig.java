package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WebClientConfig {

    @Bean
    //@LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024));

    }


    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10); // Configure the thread pool size based on your needs
    }
}
