package com.example.application.services;

import com.example.application.exceptions.*;

import com.example.application.exceptions.NumberCanNotBeChanged;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



@Service
public class SettingsService {


    @Value("${numberOfMonth}")
    private int numberOfMonth;

    @Value("${warehouseFunction}")
    private String isFunction;

    private static final String CONFIG_FILE = "./config.properties";



    public int getNumberOfMonth(){
        return this.numberOfMonth;
    }

    public String changeNumberOfMonth(int month){

            this.numberOfMonth = month;

           return "set";


    }

    public String getWarehouseOption(){
        return this.isFunction;
    }

    public String setWarehouseFunction(String func){
        if (!func.toLowerCase().equals("true") && !func.toLowerCase().equals("false")){
            return "Не правильно введены значения";
        }
        this.isFunction = func;

        return null;
    }


}