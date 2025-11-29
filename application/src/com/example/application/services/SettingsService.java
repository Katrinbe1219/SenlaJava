package com.example.application.services;

import com.example.application.exceptions.NumberCanNotBeChanged;
import com.example.application.repositories.PropertiesRepository;
import com.example.custom_annotations.Inject;

@Inject
public class SettingsService {
    @Inject
    private PropertiesRepository repo;

//    public SettingsService(PropertiesRepository repo) {
//        this.repo = repo;
//    }

    public int getNumberOfMonth(){
        return repo.getNumberOfMonth();
    }

    public String changeNumberOfMonth(String month){
        try {
            int number = parseInt(month);
            return repo.changeNumberOfMonth(number);
        } catch (NumberCanNotBeChanged e) {
            return e.getMessage();
        }

    }

    public String getWarehouseOption(){
        return repo.getWarehouseFunction();
    }

    public String setWarehouseFunction(String func){
        System.out.println(func.toLowerCase());
        if (!func.toLowerCase().equals("true") && !func.toLowerCase().equals("false")){
            return "Не правильно введны значения";
        }
        return repo.setWarehouseFunction(func);
    }

    private int parseInt(String number) throws NumberCanNotBeChanged{
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new NumberCanNotBeChanged(number);
        }
    }
}
