package com.astromyllc.shootingstar.setup;

import com.astromyllc.shootingstar.setup.config.DataBaseInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@SpringBootApplication
@RequiredArgsConstructor
@EnableDiscoveryClient
//@PropertySource("file:/app/.env")
@Slf4j
public class Setup {

    private final DataBaseInitializer dataBaseInitializer;

    public static void main(String[] args) {
        System.out.println("🔥 Spring Boot app started!");

        boolean isDocker = isRunningInDocker();

        if (isDocker) {
            System.setProperty("spring.profiles.active", "docker");
        } else {
            System.setProperty("spring.profiles.active", "local");
        }
        SpringApplication.run(Setup.class, args);
    }

    private static boolean isRunningInDocker() {
        // Check for Docker environment markers
        return System.getenv("DOCKER_ENV") != null ||
                new java.io.File("/.dockerenv").exists() ||
                System.getenv("KUBERNETES_SERVICE_HOST") != null;
    }

    // Run Database Initialization on Application Startup
    @Bean
    public CommandLineRunner initializeDatabase() {
        return args -> {
            System.out.println("🔥 Running CommandLineRunner for Setup");
            try {
                // Initialize the database (check existence or create if not exists)
                dataBaseInitializer.createDatabaseIfNotExists();
            } catch (SQLException e) {
                log.error("❌ Failed to initialize database: ", e);
            }
        };
    }
}
