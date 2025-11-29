package com.example.application.controllers;

import com.example.application.services.SettingsService;
import com.example.custom_annotations.Inject;

@Inject
public class SettingController {
    @Inject
    private SettingsService settingsService;

//    SettingController(SettingsService settingsService) {
//        this.settingsService = settingsService;
//    }

    public int getNumberOfMonth(){
        return settingsService.getNumberOfMonth();
    }

    public String changeNumberOfMonth(String number){
        return settingsService.changeNumberOfMonth(number);
    }

    public String getWarehouseOption(){
        return settingsService.getWarehouseOption();
    }

    public String setWarehouseFunction(String func){
        return settingsService.setWarehouseFunction(func);
    }


}
