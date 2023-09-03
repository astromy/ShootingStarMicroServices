package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.config;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
@EnableWebFluxSecurity
public class SpringConfig implements WebMvcConfigurer {

        @Inject
        private EntityManagerFactory entityManagerFactory;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:js/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:css/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:fonts/");
        registry.addResourceHandler("/styles/**").addResourceLocations("classpath:styles/");
        registry.addResourceHandler("/scripts/**").addResourceLocations("classpath:scripts/");
        registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:vendor/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:images/");
        registry.addResourceHandler("/reports/**").addResourceLocations("classpath:reports/");
        log.debug("Add Resource Handler Executed");
    }

}
