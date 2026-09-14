package com.petshop.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String rawUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("DATABASE_PUBLIC_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("POSTGRES_URL");
        }
        
        String username = System.getenv("SPRING_DATASOURCE_USERNAME");
        if (username == null || username.isBlank()) {
            username = System.getenv("PGUSER");
        }
        if (username == null || username.isBlank()) {
            username = System.getenv("POSTGRES_USER");
        }

        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (password == null || password.isBlank()) {
            password = System.getenv("PGPASSWORD");
        }
        if (password == null || password.isBlank()) {
            password = System.getenv("POSTGRES_PASSWORD");
        }

        String jdbcUrl = "jdbc:postgresql://localhost:5432/petshop_db";

        if (rawUrl != null && !rawUrl.isBlank()) {
            String processedUrl = rawUrl.trim();
            if (processedUrl.startsWith("postgres://") || processedUrl.startsWith("postgresql://") || processedUrl.startsWith("jdbc:postgresql://")) {
                try {
                    String uriString = processedUrl;
                    if (uriString.startsWith("jdbc:")) {
                        uriString = uriString.substring(5);
                    }
                    if (uriString.startsWith("postgres://")) {
                        uriString = "postgresql://" + uriString.substring("postgres://".length());
                    }

                    URI uri = new URI(uriString);
                    String host = uri.getHost();
                    int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                    String path = uri.getPath();
                    String dbName = (path != null && path.length() > 1) ? path.substring(1) : "railway";
                    String query = uri.getQuery();

                    jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
                    if (query != null && !query.isBlank()) {
                        jdbcUrl += "?" + query;
                    }

                    if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                        String[] userInfo = uri.getUserInfo().split(":", 2);
                        if (userInfo.length > 0 && userInfo[0] != null && !userInfo[0].isBlank()) {
                            if (username == null || username.isBlank() || username.equals("postgres")) {
                                username = userInfo[0];
                            }
                        }
                        if (userInfo.length > 1 && userInfo[1] != null && !userInfo[1].isBlank()) {
                            if (password == null || password.isBlank() || password.equals("postgres")) {
                                password = userInfo[1];
                            }
                        }
                    }
                } catch (Exception e) {
                    if (!processedUrl.startsWith("jdbc:")) {
                        jdbcUrl = "jdbc:" + processedUrl;
                    } else {
                        jdbcUrl = processedUrl;
                    }
                }
            } else {
                if (!processedUrl.startsWith("jdbc:")) {
                    jdbcUrl = "jdbc:" + processedUrl;
                } else {
                    jdbcUrl = processedUrl;
                }
            }
        }

        if (username == null || username.isBlank()) {
            username = "postgres";
        }
        if (password == null || password.isBlank()) {
            password = "postgres";
        }

        System.out.println("=== PETSHOP DATABASE CONNECTING TO: " + jdbcUrl + " WITH USER: " + username + " ===");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setInitializationFailTimeout(60000);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }
}