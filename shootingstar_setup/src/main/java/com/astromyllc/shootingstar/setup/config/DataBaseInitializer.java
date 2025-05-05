package com.astromyllc.shootingstar.setup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class DataBaseInitializer {

    @Value("${data.connection}")
    private String dbConnection;

    private final String DB_NAME = "shootingstarsetup";  // The name of the database to check/create

    public void createDatabaseIfNotExists() throws SQLException {
        String url = "jdbc:postgresql://" + dbConnection + "/postgres";  // Connect to the default 'postgres' DB
        String user = "astromyllc";
        String pass = "IdowhatIlikeIlikewhatIdo!@3";

        // Step 1: Check if the database exists
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'");
            if (!rs.next()) {
                // Step 2: If the database doesn't exist, create it
                stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("✅ Database '" + DB_NAME + "' created successfully.");
            } else {
                System.out.println("⚡ Database '" + DB_NAME + "' already exists.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error while checking/creating database: " + e.getMessage());
            throw e;  // Re-throw exception to notify caller
        }

        // Step 3: Once the database is created (or exists), configure Hibernate to work with it
        connectToDatabase(DB_NAME);
    }

    private void connectToDatabase(String dbName) throws SQLException {
        // Create a new connection to the actual database we want to use
        String url = "jdbc:postgresql://" + dbConnection + "/" + dbName;
        String user = "astromyllc";
        String pass = "IdowhatIlikeIlikewhatIdo!@3";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            // Step 4: Once connected, you can proceed with any database setup logic or let Hibernate handle the schema
            System.out.println("✅ Successfully connected to the database: " + dbName);
        } catch (SQLException e) {
            System.err.println("❌ Error while connecting to the database: " + e.getMessage());
            throw e;  // Re-throw exception to notify caller
        }
    }
}

