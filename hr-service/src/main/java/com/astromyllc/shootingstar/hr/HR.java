package com.astromyllc.shootingstar.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
//@PropertySource("file:/app/.env")

public class HR {
    public static void main(String[] args) {
        SpringApplication.run(HR.class, args);

        boolean isDocker = isRunningInDocker();

        if (isDocker) {
            System.setProperty("spring.profiles.active", "docker");
        } else {
            System.setProperty("spring.profiles.active", "local");
        }
    }

    private static boolean isRunningInDocker() {
        // Check for Docker environment markers
        return System.getenv("DOCKER_ENV") != null ||
                new java.io.File("/.dockerenv").exists() ||
                System.getenv("KUBERNETES_SERVICE_HOST") != null;
    }
}
