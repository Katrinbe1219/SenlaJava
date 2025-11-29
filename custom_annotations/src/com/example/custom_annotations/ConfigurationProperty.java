package com.example.custom_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperty {
    String configFileName() default "config.properties";
    String propertyName() default "PropertiesRepository.Properties";
    String type() default "properties";

}