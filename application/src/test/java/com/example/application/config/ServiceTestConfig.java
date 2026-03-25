package com.example.application.config;

import com.example.application.services.SettingsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

@Configuration
public class ServiceTestConfig {

    @Bean
    public SettingsService settingsServiceNotMock(){
        return new SettingsService();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("numberOfMonth", "3");
        props.setProperty("warehouseFunction","true");
        configurer.setProperties(props);
        return configurer;
    }

}
