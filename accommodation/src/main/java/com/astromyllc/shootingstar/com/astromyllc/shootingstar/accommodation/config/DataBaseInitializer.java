package com.astromyllc.shootingstar.com.astromyllc.shootingstar.accommodation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class DataBaseInitializer {

    @Value("${data.connection}")
    private String dbConnection;

    public void createDatabaseIfNotExists() throws SQLException {
        String url = "jdbc:postgresql://" + dbConnection +"/postgres";
        String user = "astromyllc";
        String pass = "IdowhatIlikeIlikewhatIdo!@3";
        String dbName = "accommodation";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + dbName);
                System.out.println("✅ Database '" + dbName + "' created successfully.");
            } else {
                System.out.println("⚡ Database '" + dbName + "' already exists.");
            }
        }
    }
}

