package com.petshop.backend.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_PUBLIC_URL");
        }
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:postgresql://localhost:5432/petshop_db";
        }

        if (dbUrl.startsWith("postgresql://")) {
            dbUrl = "jdbc:" + dbUrl;
        } else if (dbUrl.startsWith("postgres://")) {
            dbUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
        }

        properties.setUrl(dbUrl);

        String username = System.getenv("SPRING_DATASOURCE_USERNAME");
        if (username == null || username.isBlank()) {
            username = System.getenv("PGUSER");
        }
        if (username == null || username.isBlank()) {
            username = "postgres";
        }
        properties.setUsername(username);

        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (password == null || password.isBlank()) {
            password = System.getenv("PGPASSWORD");
        }
        if (password == null || password.isBlank()) {
            password = "postgres";
        }
        properties.setPassword(password);

        properties.setDriverClassName("org.postgresql.Driver");
        return properties;
    }
}