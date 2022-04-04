package com.demo.provisioner.util;

import net.snowflake.client.jdbc.SnowflakeBasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProvisionerConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    Environment env;

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        String jdbcUsername = env.getProperty("sf.user");
        String jdbcPassword = env.getProperty("sf.pwd");
        String jdbcUrl = env.getProperty("sf.jdbc.url");
        SnowflakeBasicDataSource basicDataSource = new SnowflakeBasicDataSource();
        basicDataSource.setSsl(true);
        basicDataSource.setUser(jdbcUsername);
        basicDataSource.setPassword(jdbcPassword);
        basicDataSource.setUrl(jdbcUrl);
        return new JdbcTemplate(basicDataSource);
    }
}