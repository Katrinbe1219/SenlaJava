package  com.example.application.controllers;

import com.example.application.services.SettingsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class SettingController {
    private SettingsService settingsService;

    public SettingController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }


    @GetMapping(value="/month", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getNumberOfMonth(){
        return settingsService.getNumberOfMonth();
    }

    @PatchMapping(value="/change-number", produces = "text/plain; charset=UTF-8")
    public String changeNumberOfMonth(@RequestParam("number") int number){
        //curl -X PATCH "http://localhost:8080/senla/settings/change-number?number=10"
        return settingsService.changeNumberOfMonth(number);
    }

    @GetMapping(value = "/warehouse", produces="text/plain")
    public String getWarehouseOption(){
        return settingsService.getWarehouseOption();
    }

    @PatchMapping(value="/change-warehouse", produces = "text/plain; charset=UTF-8")
    public String setWarehouseFunction(@RequestParam("func") String func){
        return settingsService.setWarehouseFunction(func);
    }


}
