package com.example.processing_annotations;

import com.example.custom_annotations.ConfigurableClass;
import com.example.custom_annotations.ConfigurationProperty;
import com.example.custom_annotations.Inject;

import java.lang.module.Configuration;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InjectAnnotationProcessor {
    private Map<Class<?>, Object> singletons;
    private ConfigurationAnnotationProcessor annotationProcessor;

    private static  InjectAnnotationProcessor instance;
    private InjectAnnotationProcessor() {
        singletons = new HashMap<>();
        annotationProcessor = new ConfigurationAnnotationProcessor();
    }

    // хранятся уже готовые обьекты
    public <T> void registerSingleton(Class<T> clazz, T obj) {
        singletons.put(clazz, obj);
    }



    public synchronized  static InjectAnnotationProcessor getInstance() {
        if (instance == null) {
            instance = new InjectAnnotationProcessor();
        }
        return instance;
    }

    public <T> T getSingleton(Class<T> clazz) {
        return (T) singletons.get(clazz);
    }

    // передается тип зависимости
    public <T> T getInstance(Class<T> clazz)  throws  Exception{

        if (!clazz.isAnnotationPresent(Inject.class) && !clazz.isAnnotationPresent(ConfigurableClass.class)) {
            throw new Exception("Класс не содержит никаких аннотаций " + clazz.getName());
        }
        // если уже существует такой экземляр
        if (singletons.containsKey(clazz)){
            return  (T) singletons.get(clazz);
        }

        // проходимся по полям, которые необходимо внедрить
        T newInstance  = (T)  clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        Class<?> fieldClass;
        Object dependency;



        for (Field field : fields) {
            // проверка на аннотацию - для внедрения

            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                // получаем тип поля
                 fieldClass= field.getType();
                 // вытаскиваем зависимость
                dependency = singletons.get(fieldClass);
                // если зависимости нет, значит мы ее не добавляли в наш контейнер
                if (dependency == null) throw new Exception("зависимость не добавлен в контейнер");
                else field.set(newInstance, singletons.get(fieldClass));
            }else if (field.isAnnotationPresent(ConfigurationProperty.class)) {
                 field.setAccessible(true);
                 annotationProcessor.loadProperties(newInstance);
            }
        }

        singletons.put(clazz, newInstance);

        return newInstance;

    }



}
