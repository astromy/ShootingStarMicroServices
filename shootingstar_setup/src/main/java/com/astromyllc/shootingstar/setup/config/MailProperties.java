package com.astromyllc.shootingstar.setup.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mail")
@Data
public class MailProperties {
    private int rateLimit;
    private int queueCapacity;
    private int maxRetries;
    private long retryDelay;
}