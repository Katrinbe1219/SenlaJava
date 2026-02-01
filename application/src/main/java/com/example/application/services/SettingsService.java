package com.example.application.services;

import com.example.application.exceptions.*;
import com.example.custom_applications.Inject;
import com.example.custom_applications.ConfigurableClass;
import com.example.custom_applications.ConfigurationProperty;
import com.example.application.exceptions.NumberCanNotBeChanged;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.Properties;


@Component

public class SettingsService {

    //@ConfigurationProperty(propertyName = "numberOfMonth", type = "int")
    @Value("${numberOfMonth}")
    private int numberOfMonth;

    //@ConfigurationProperty(propertyName = "warehouseFunction", type = "String")
    @Value("${warehouseFunction}")
    private String isFunction;

    private static final String CONFIG_FILE = "config.properties";



    public int getNumberOfMonth(){
        return this.numberOfMonth;
    }

    public String changeNumberOfMonth(String month){
        try {
            int number = parseInt(month);

            this.numberOfMonth = number;
            saveChanges();

           return "";
        } catch ( NumberCanNotBeChanged e) {
            return e.getMessage();
        }

    }

    public String getWarehouseOption(){
        return this.isFunction;
    }

    public String setWarehouseFunction(String func){
        if (!func.toLowerCase().equals("true") && !func.toLowerCase().equals("false")){
            return "Не правильно введены значения";
        }
        this.isFunction = func;
        saveChanges();
        return null;
    }

    private int parseInt(String number) throws NumberCanNotBeChanged{
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new NumberCanNotBeChanged(number);
        }
    }

    private void saveChanges(){
        Properties prop = new Properties();
        prop.setProperty("numberOfMonth", String.valueOf(numberOfMonth));
        prop.setProperty("warehouseFunction", isFunction);
        try (FileOutputStream file = new FileOutputStream(CONFIG_FILE)){
            prop.store(file, "BookStore Configuration");
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
