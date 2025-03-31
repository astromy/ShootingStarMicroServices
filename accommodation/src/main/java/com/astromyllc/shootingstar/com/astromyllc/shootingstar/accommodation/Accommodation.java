package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.config.DataBaseInitializer;
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
@Slf4j
public class Accommodation {

    private final DataBaseInitializer dataBaseInitializer;
    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Accommodation.class,args);
    }


    @Bean
    public CommandLineRunner initializeDatabase() {
        return args -> {
            try {
                dataBaseInitializer.createDatabaseIfNotExists();
            } catch (SQLException e) {
                log.error("❌ Failed to initialize database: ", e);
            }
        };
    }

}
