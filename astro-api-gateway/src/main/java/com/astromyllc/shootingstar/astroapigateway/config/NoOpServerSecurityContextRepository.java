/*
package com.astromyllc.shootingstar.astroapigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NoOpServerSecurityContextRepository implements ServerSecurityContextRepository {

    private static final NoOpServerSecurityContextRepository INSTANCE = new NoOpServerSecurityContextRepository();

    public static NoOpServerSecurityContextRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Bean
    public ReactiveAuthenticationManager noopAuthenticationManager() {
        return authentication -> Mono.error(new IllegalStateException("Authentication should never be called"));
    }
}*/
