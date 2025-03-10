package com.astromyllc.astroorb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                // Require authentication for all requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/resources/**").permitAll()
                        .anyRequest().authenticated()  // Require authentication for all other requests
                )
                // OAuth2 Login configuration using Keycloak
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/ShootingStar")  // Redirect to Keycloak
                )
                // Handle unauthorized access by redirecting to login
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/ShootingStar"))
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                // Session management to handle expired sessions
                .sessionManagement(session -> session
                        .sessionFixation().newSession()
                        .invalidSessionUrl("/oauth2/authorization/ShootingStar")  // Redirect to login when session expires
                );

        return http.build();
    }

    // OIDC logout handler to redirect to Keycloak's logout endpoint
    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        // Specify the post-logout redirect URI (change this to the correct value)
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("https://orb.astromyllc.com");

        return oidcLogoutSuccessHandler;
    }
}
