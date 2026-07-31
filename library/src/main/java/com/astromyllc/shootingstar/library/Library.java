package com.astromyllc.shootingstar.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
@EnableDiscoveryClient
public class Library {
    public static void main(String[] args) {
        boolean isDocker = isRunningInDocker();

        if (isDocker) {
            System.setProperty("spring.profiles.active", "docker");
        } else {
            System.setProperty("spring.profiles.active", "local");
        }

        SpringApplication.run(Library.class, args);
    }

    private static boolean isRunningInDocker() {
        // Check for Docker environment markers
        return System.getenv("DOCKER_ENV") != null ||
                new java.io.File("/.dockerenv").exists() ||
                System.getenv("KUBERNETES_SERVICE_HOST") != null;
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
