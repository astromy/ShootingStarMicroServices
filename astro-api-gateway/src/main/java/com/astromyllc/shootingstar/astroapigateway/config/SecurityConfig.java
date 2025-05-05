
package com.astromyllc.shootingstar.astroapigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
//@EnableWebFluxSecurity  // Required for Reactive Security
public class SecurityConfig {
  /*  @Bean
    public SecurityWebFilterChain disableSecurityCompletely(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .headers(headers -> headers
                        .contentTypeOptions(ServerHttpSecurity.HeaderSpec.ContentTypeOptionsSpec::disable)
                        .xssProtection(ServerHttpSecurity.HeaderSpec.XssProtectionSpec::disable)
                        //.cacheControl(ServerHttpSecurity.HeaderSpec.CacheControlSpec::disable)
                        .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
                )
                .requestCache(ServerHttpSecurity.RequestCacheSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .build();
    }*/
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