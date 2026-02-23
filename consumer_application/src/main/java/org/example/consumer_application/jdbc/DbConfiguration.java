package org.example.consumer_application.jdbc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:./application.properties")
@EnableJdbcRepositories
public class DbConfiguration {

    @Value("${db.url}")
    private String url;

    @Value("${db.password}")
    private String password;

    @Value("${db.user}")
    private String user;

    @Value("${driver}")
    private String driver;

    @Bean
    public DataSource getDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(){
        return new NamedParameterJdbcTemplate(getDataSource());
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(){
        return new DataSourceTransactionManager(getDataSource());
    }
}


