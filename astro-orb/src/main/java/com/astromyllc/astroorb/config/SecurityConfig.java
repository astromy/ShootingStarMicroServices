package com.astromyllc.astroorb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                // OAuth2 Login configuration using Keycloak
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/ShootingStar")
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                // Handle unauthorized access by redirecting to the login page
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // Authorize requests
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**", "/resources/**").permitAll()  // Use requestMatchers instead of antMatchers
                        .anyRequest().authenticated()
                )
                // Session management to handle expired sessions
                .sessionManagement(session -> session
                        .sessionFixation().newSession()
                        .invalidSessionUrl("/oauth2/authorization/ShootingStar")  // Redirect to login when session expires
                );
        ;

        return http.build();
    }

    // OIDC logout handler to redirect to Keycloak's logout endpoint
    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        // Specify the post-logout redirect URI
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("http://localhost:7013");

        return oidcLogoutSuccessHandler;
    }
}
