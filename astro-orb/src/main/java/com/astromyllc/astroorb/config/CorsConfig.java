/*
package com.astromyllc.astroorb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow your frontend origin
        config.addAllowedOrigin("http://localhost:7013"); // Your frontend
        config.addAllowedOrigin("https://orb.astromyllc.com"); // Production

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Allow all HTTP methods
        config.addAllowedMethod("*");

        // Allow all headers
        config.addAllowedHeader("*");

        // Expose headers to the browser
        config.addExposedHeader("Authorization");

        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}*/
