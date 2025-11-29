package com.example.processing_annotations;

import com.example.custom_annotations.ConfigurableClass;
import com.example.custom_annotations.ConfigurationProperty;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Properties;



public class ConfigurationAnnotationProcessor {
   private Properties properties;

   public ConfigurationAnnotationProcessor() {
       properties = new Properties();
   }

   public void loadProperties(Object obj) throws Exception{
       Class<?> classInstance = obj.getClass();

       if (! classInstance.isAnnotationPresent(ConfigurableClass.class)){
           throw new IllegalArgumentException("Class " + classInstance.getName() + " is not annotated with com.example.custom_annotations.ConfigurableClass");
       }

       String originalFile = classInstance.getAnnotation(ConfigurableClass.class).configPath();
        createOrGetProperties(originalFile);
       // по заданию у каждого configProperty должен быть configPath,
       // но у меня везде и уж точно в одном классе config один, так что заранее идет загрузка файла
       // поэтому проверки потом у каждого поля нет на путь к файлу
       // он идет на уровне класса

       String type;
       ConfigurationProperty annotation;
       String propertyName;
       String configName;

       for (Field field: classInstance.getDeclaredFields()){
           if (field.isAnnotationPresent(ConfigurationProperty.class)){
               annotation = field.getAnnotation(ConfigurationProperty.class);
               type = annotation.type();
               propertyName = annotation.propertyName();
               configName = annotation.configFileName();
               if (!type.equals("properties")){
                   throw  new IllegalArgumentException("тип поля должен быть properties");
               }
               field.setAccessible(true);
               setField(field, obj);
           }
       }
   }

   private void createOrGetProperties(String fileName){
       File file = new File(fileName);

       if (!file.exists()){
           properties.setProperty("numberOfMonth", "6" );
           properties.setProperty("warehouseFunction", "true" );

           try (OutputStream output = new FileOutputStream(file)){
               properties.store(output, "BookStore Configuration");
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       }else{
           try(InputStream input = new FileInputStream(file)){
               properties.load(input);
           }catch (IOException e){
               throw new RuntimeException(e);
           }
       }
   }

   private void setField(Field field,  Object instance ) throws IllegalAccessException {
       // у меня заполняется репозиторий PropertiesRepository with variable type properties
       if (field.getType() == Properties.class){
           field.set(instance, this.properties);
       }else{
           throw new IllegalArgumentException("Class " + field.getType().getName() + " is not with type Properties");
       }
   }
}