package com.example.application.spring;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:./liquibase.properties")
public class LiquibaseConfiguration {

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Value("${url}")
    private String url;

    @Value("${changelog}")
    private String changelog;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName("org.postgresql.Driver");
        System.out.println(url + " " + this.user + " " + password );
        datasource.setUrl(url);
        datasource.setUsername(user);
        datasource.setPassword(password);

        return datasource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        liquibase.setChangeLog(changelog);
        return liquibase;
    }
}
