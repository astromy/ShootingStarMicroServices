/*
package com.astromyllc.shootingstar.astroapigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  //  private static final String issuerUri = "http://localhost:8090/realms/ShootingStar";
    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChain2(ServerHttpSecurity serverHttpSecurity) throws Exception {
        serverHttpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange.pathMatchers("/eureka/**","/api/setup/signupInstitution")
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        return serverHttpSecurity.build();
    }

   */
/* @Autowired()
    @Qualifier("clientRegistrationRepository")
    private ReactiveClientRegistrationRepository clientRegistrationRepository;*//*


   */
/* @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity

                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/", "/error", "/index","/api/setup/signupInstitution").permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .logout((logout) ->
                        logout
                                // configures how log out is done
                                .logoutHandler(logoutHandler())
                                // log out will be performed on POST /signout
                                .logoutUrl("/signout")
                                // configure what is done on logout success
                                .logoutSuccessHandler(oidcLogoutSuccessHandler())
                )
                .csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }*//*



    private ServerLogoutHandler logoutHandler() {
        return new DelegatingServerLogoutHandler(new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler());
    }

  */
/*  private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri(FRONTEND_URL);
        return logoutSuccessHandler;
    }*//*


   */
/* @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }*//*

}
*/
