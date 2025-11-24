package task_3_4.controllers;

import task_3_4.services.SettingsService;

public class SettingController {
    private SettingsService settingsService;

    SettingController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

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
