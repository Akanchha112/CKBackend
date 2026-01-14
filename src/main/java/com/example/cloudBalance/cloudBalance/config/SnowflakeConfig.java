package com.example.cloudBalance.cloudBalance.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SnowflakeConfig {

    @Bean(name = "snowflakeDataSource")
    // DO NOT add @Primary here - we want MySQL to be primary
    public DataSource snowflakeDataSource(
            @Value("${snowflake.url}") String url,
            @Value("${snowflake.username}") String username,
            @Value("${snowflake.password}") String password,
            @Value("${snowflake.warehouse}") String warehouse,
            @Value("${snowflake.database}") String database,
            @Value("${snowflake.schema}") String schema,
            @Value("${snowflake.role}") String role
    ) {
        Properties props = new Properties();
        props.put("user", username);
        props.put("password", password);
        props.put("warehouse", warehouse);
        props.put("db", database);
        props.put("schema", schema);
        props.put("role", role);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setDataSourceProperties(props);
        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        config.setMaximumPoolSize(5);
        config.setPoolName("SnowflakePool");

        return new HikariDataSource(config);
    }

    @Bean(name = "snowflakeJdbcTemplate")
    public JdbcTemplate snowflakeJdbcTemplate(
            @Qualifier("snowflakeDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}