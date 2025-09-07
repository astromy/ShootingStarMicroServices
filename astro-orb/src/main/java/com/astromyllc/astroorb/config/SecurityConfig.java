package com.astromyllc.astroorb.config;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                // Require authentication for all requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/resources/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/webhook/subscriptionPaymentStatus").permitAll()
                        .anyRequest().authenticated()
                )
                // Enable CSRF with Cookie-based token storage
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/webhook/subscriptionPaymentStatus", "/public/**")
                )
                // OAuth2 Login configuration using Keycloak
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/ShootingStar")
                        .defaultSuccessUrl("/", true)
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                )
                // Handle unauthorized access by redirecting to login
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/ShootingStar"))
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(loggingOidcLogoutSuccessHandler(clientRegistrationRepository))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                )
                // Enhanced Session management
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId()
                        .invalidSessionUrl("/oauth2/authorization/ShootingStar?invalidSession=true")
                        .maximumSessions(1)
                        .expiredSessionStrategy(new CustomSessionExpiredStrategy())
                );

        return http.build();
    }

    // Custom session expired strategy
    private static class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {
        @Override
        public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
            HttpServletResponse response = event.getResponse();
            response.sendRedirect("/oauth2/authorization/ShootingStar?sessionExpired=true");
        }
    }

    // OIDC logout handler to redirect to Keycloak's logout endpoint
    private LogoutSuccessHandler loggingOidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler logoutHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        logoutHandler.setPostLogoutRedirectUri("{baseUrl}/");

        return (request, response, authentication) -> {
            String baseUrl = ServletUriComponentsBuilder.fromRequest(request)
                    .replacePath(null)
                    .build()
                    .toUriString();
            System.out.println("Logging out with base URL: " + baseUrl);
            logoutHandler.onLogoutSuccess(request, response, authentication);
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String issuerUri = "https://keycloak.astromyllc.com/realms/ShootingStar";
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator("orb_frontend");
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }
}