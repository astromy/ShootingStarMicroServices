package com.astromyllc.astroorb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Split out of WebConfig for the same reason as WebClientConfig: this bean used to
 * be defined on WebConfig itself, but WebConfig also injects
 * {@code SubscriptionEnforcementInterceptor}, which takes an {@code ObjectMapper} in
 * its constructor. With {@code @Primary} making this the only candidate, Spring
 * needed WebConfig fully built to produce it before it could build
 * SubscriptionEnforcementInterceptor - which WebConfig needs first. Same cycle
 * shape as the WebClient.Builder issue, different bean. Keeping it here, with no
 * dependency back on WebConfig, avoids it.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
