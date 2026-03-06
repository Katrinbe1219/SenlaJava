package org.example.application.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:./application.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = "org.example.application")
public class ConfigurationHibernate {

    @Value("${db.url}")
    String url;

    @Value("${db.user}")
    String user;

    @Value("${db.password}")
    String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource manager = new DriverManagerDataSource();
        manager.setDriverClassName("org.postgresql.Driver");
        manager.setUsername(user);
        manager.setPassword(password);
        manager.setUrl(url);
        return manager;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(
            DataSource dataSource
    ) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("org.example.application");
        factory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        return factory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(
            LocalSessionFactoryBean sf
    ){
        return new HibernateTransactionManager(sf.getObject());
    }
}
