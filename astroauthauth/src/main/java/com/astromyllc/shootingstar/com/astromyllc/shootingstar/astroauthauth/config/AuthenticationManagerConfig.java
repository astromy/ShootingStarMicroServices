package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.config;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.AuthenticationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AuthenticationManagerConfig implements ReactiveAuthenticationManager   {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationServiceInterface authenticationServiceInterface;


    @Bean("2")
    public AuthenticationManager authenticationManager() throws Exception {
        AuthenticationManager authenticationManager=new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                authentication= SecurityContextHolder.getContext().getAuthentication();
                return authentication;
            }
        };

        /*return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();*/

        return authenticationManager;
    }

    //@Override
 /*  @Bean
    protected AuthenticationManagerBuilder configure(AuthenticationManagerBuilder auth) throws Exception {
     return    auth.authenticationProvider(authenticationProvider());
    }*/

 /*   @Bean
    public ProviderManager authManagerBean(AuthenticationProvider provider) {
        return new ProviderManager(provider);
    }*/

    /*@Bean("2nd")
    public ReactiveAuthenticationManager authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authenticationServiceInterface.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return (ReactiveAuthenticationManager) authProvider;
    }*/

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        authentication= SecurityContextHolder.getContext().getAuthentication();
        return (Mono<Authentication>) authentication;
    }
}
