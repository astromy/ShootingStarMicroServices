package com.astromyllc.shootingstar.astroapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
//@PropertySource("file:/app/.env")
public class ApiGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);

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
