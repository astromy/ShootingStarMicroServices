package com.astromyllc.shootingstar.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@RequiredArgsConstructor
@EnableDiscoveryClient
@Slf4j

//@PropertySource("file:/app/.env")
public class Finance {

    public static void main(String[] args) {
        SpringApplication.run(Finance.class, args);

        boolean isDocker = isRunningInDocker();

        if (isDocker) {
            System.setProperty("spring.profiles.active", "docker");
            // System.setProperty("spring.config.import", "file:/app/.env");
        } else {
            System.setProperty("spring.profiles.active", "local");
            // System.setProperty("spring.config.import", "optional:file:.env,optional:file:../.env");
        }
    }

    private static boolean isRunningInDocker() {
        // Check for Docker environment markers
        return System.getenv("DOCKER_ENV") != null ||
                new java.io.File("/.dockerenv").exists() ||
                System.getenv("KUBERNETES_SERVICE_HOST") != null;
    }
}
