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

               field.setAccessible(true);
               setField(field, obj, type, propertyName);
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

   private void setField(Field field,  Object instance, String type, String propertyName ) throws IllegalAccessException {
       String value = properties.getProperty(propertyName);
       if (type.toLowerCase().equals("string")){
           field.set(instance, value);
       }else if (type.toLowerCase().equals("int")){
           int intValue = parseInt(value);
           field.set(instance, intValue);

       } else{
           throw new IllegalArgumentException("Class " + field.getType().getName() + " is not with appropriate type");
       }
   }

    private int parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            // возвращаю дефолтный
            System.out.println("При попытке обработки int переменной в properties произошла ошибка. Полю было присвоено дефолтное значение");
            return 6;
        }
    }
}