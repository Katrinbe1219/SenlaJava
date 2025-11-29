package com.example.application.repositories;

import com.example.custom_annotations.ConfigurableClass;
import com.example.custom_annotations.ConfigurationProperty;
import com.example.custom_annotations.Inject;

import java.io.*;
import java.util.Properties;

@Inject
@ConfigurableClass(configPath = "config.properties")
public class PropertiesRepository {
    @ConfigurationProperty(configFileName = "config.properties")
    Properties properties;
    private static final String CONFIG_FILE = "config.properties";

    // если вызовется ошибка, она дойдет до Test3, где она обрабатывается
    // Если она есть, то система не работает, так как необходимо иметь доступ к конфигурации
    public PropertiesRepository() throws Exception {
//
//        properties = new Properties();
//        File file = new File(CONFIG_FILE);
//
//        if (!file.exists()) {
//            throw new  Exception("файл с пропертиес не был создан");
//        }else {
//            try (InputStream input = new FileInputStream(file)){
//                properties.load(input);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    // все поля должны существовать - в конструкторе все прописано
    // поэтому не делается проверка на существование ключа
    public int getNumberOfMonth(){
        String value = properties.getProperty("numberOfMonth");
        try{
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Number of Month is not a number. Returned default value, change settings");
            return 6;
        }

    }


    public String getWarehouseFunction(){
        return properties.getProperty("warehouseFunction");
    }

    public String setWarehouseFunction(String func){
        properties.setProperty("warehouseFunction", func);
        saveChanges();
        return "";
    }

    public String changeNumberOfMonth(int number){
        try {
            properties.setProperty("numberOfMonth", String.valueOf(number));
            saveChanges();
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    private void saveChanges(){
        try (FileOutputStream file= new FileOutputStream(CONFIG_FILE)){
                properties.store(file, "BookStore Configuration");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
