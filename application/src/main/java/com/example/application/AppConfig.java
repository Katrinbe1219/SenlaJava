package com.example.application;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableWebMvc
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan(basePackages = "com.example.application")
@PropertySource("classpath:config.properties")
public class AppConfig implements WebMvcConfigurer {
    @PostConstruct
    public void init() {
        System.out.println("=== SPRING APPCONFIG LOADED ===");
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory factory) {
        return new HibernateTransactionManager(factory); // Управляет сессиями
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();

        factory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        factory.setDataSource(dataSource);

        return factory;
    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            DataSource dataSource
//    ){
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setDataSource(dataSource);
//
//        emf.setPackagesToScan("com.example.application");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//        vendorAdapter.setShowSql(true);
//        emf.setJpaVendorAdapter(vendorAdapter);
//
//        Properties props = new Properties();
//        props.put("hibernate.show_sql", "true");
//        props.put("hibernate.format_sql", "true");
//        props.put("hibernate.hbm2ddl.auto", "none");
//        props.put("hibernate.ddl-auto", "none");
//        props.put("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");
//        props.put("hibernate.connection.acquisition_mode", "IMMEDIATE");
//        props.put("hibernate.connection.release_mode", "AFTER_TRANSACTION");
//        props.put("hibernate.connection.provider_class",
//                "org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl");
//        emf.setJpaProperties(props);
//        return emf;
//    }
//
//
//
//    @Bean
//    public PlatformTransactionManager transactionManager(
//            EntityManagerFactory entityManagerFactory,
//            DataSource dataSource
//    ){
//        JpaTransactionManager manager = new JpaTransactionManager();
//        manager.setEntityManagerFactory(entityManagerFactory);
//        manager.setDataSource(dataSource);
//
//        return manager;
//
//    }
}
