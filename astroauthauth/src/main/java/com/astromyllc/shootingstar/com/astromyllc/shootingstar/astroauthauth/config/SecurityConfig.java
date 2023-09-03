package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.config;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service.AuthenticationService;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.ldap.EmbeddedLdapServerContextSourceFactoryBean;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;

import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {
    @Inject
    private PasswordEncoder passwordEncoder;

    private  final AuthenticationService uthService;
   /* @Inject
    @Qualifier("authenticationService")
    private UserDetailsService userDetailsService;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
    }*/



    @Bean
    MapReactiveUserDetailsService mapReactiveUserDetailsService(PasswordEncoder encoder){
        UserDetails userDetails=uthService.loadUserByUsernameVal();
        return new MapReactiveUserDetailsService(userDetails);
    }

/*   @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
        log.debug("SecurityConfig User Configure Global Executed");
    }*/

   /* @Inject
    @Override
    @Bean
    public AuthenticationManagerBuilder authenticationManagerBean (AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
        log.debug("SecurityConfig User Configure Global Executed");
        return auth;
    }*/
   /* @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager((Collection<UserDetails>) userDetailsService);
    }*/

/*    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }*/


    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize

                        //.requestMatchers("/*").permitAll()
                        .requestMatchers("/*").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                )
                .rememberMe(Customizer.withDefaults());

        return http.build();
    }
*/

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/blog/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(withDefaults()));

        return http.build();
    }
/*
    @Bean
    SecurityWebFilterChain http(ServerHttpSecurity http) throws Exception {
        DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(
                new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler()
        );

        http
                .authorizeExchange((exchange) -> exchange.anyExchange().authenticated())
                .logout((logout) -> logout.logoutHandler(logoutHandler));

        return http.build();
    }
*/

/*
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }*/
}
