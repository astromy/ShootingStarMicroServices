package com.astromyllc.shootingstar.finance.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.*;

@Configuration
@Profile("!docker")
public class DataBaseInitializer {

    private static final String DB_NAME = "shootingstarfinance";

    @Value("${data.connection}")
    private String dbConnection;

    @Value("${DB_USERNAME}")
    private String dbUserName;

    @Value("${DB_PASSWORD}")
    private String dbpass;

    @Bean
    @Primary
    public DataSource dataSource() {
        createDatabaseIfNotExists();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + dbConnection + "/" + DB_NAME);
        config.setUsername(dbUserName);
        config.setPassword(dbpass);
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    public void createDatabaseIfNotExists() {
        String adminUrl = "jdbc:postgresql://" + dbConnection + "/postgres";

        try (Connection conn = DriverManager.getConnection(adminUrl, dbUserName, dbpass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'"
            );
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("✅ Database '" + DB_NAME + "' created successfully.");
            } else {
                System.out.println("⚡ Database '" + DB_NAME + "' already exists.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to create database: " + e.getMessage(), e);
        }
    }
}