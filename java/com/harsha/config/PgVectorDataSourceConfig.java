package com.harsha.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 * PgVectorDataSourceConfig — FIXED VERSION
 *
 * WHY THIS FILE CHANGED:
 * When you define any DataSource bean manually, Spring Boot's auto-configuration
 * for MySQL BACKS OFF completely. This caused JPA to fail with:
 *   "jdbcUrl is required with driverClassName"
 *
 * THE FIX:
 * We now explicitly define BOTH datasources:
 *   1. mysqlDataSource  → @Primary → used by JPA and all your existing code
 *   2. pgDataSource     → used ONLY by FaqEmbeddingService for pgVector
 */
@Configuration
public class PgVectorDataSourceConfig {

    // ── PRIMARY: MySQL ────────────────────────────────────────────────────────
    // @Primary = "when JPA/Hibernate asks for a DataSource, give them this one"
    // Reads from: spring.datasource.* in application.properties
    // Used by:    JPA, Hibernate, all your existing repositories — UNCHANGED
    @Bean(name = "mysqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    // ── SECONDARY: PostgreSQL / pgVector ─────────────────────────────────────
    // No @Primary — only injected where explicitly requested with @Qualifier
    // Reads from: pgvector.datasource.* in application.properties
    // Used by:    FaqEmbeddingService only
    @Bean(name = "pgDataSource")
    @ConfigurationProperties(prefix = "pgvector.datasource")
    public DataSource pgDataSource() {
        return DataSourceBuilder.create().build();
    }

    // JdbcTemplate pointed at PostgreSQL — only FaqEmbeddingService uses this
    @Bean(name = "pgJdbcTemplate")
    public JdbcTemplate pgJdbcTemplate(
            @Qualifier("pgDataSource") DataSource pgDataSource) {
        return new JdbcTemplate(pgDataSource);
    }

    // RestTemplate for HTTP calls to Ollama
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}