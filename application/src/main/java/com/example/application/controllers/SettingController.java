package  com.example.application.controllers;

import com.example.application.services.SettingsService;
import org.springframework.stereotype.Service;

@Service
public class SettingController {
    private SettingsService settingsService;

    public SettingController(SettingsService settingsService) {
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
