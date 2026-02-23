package  com.example.application.controllers;

import com.example.application.dto.StrinResponse;
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


    @GetMapping(value="/month")
    public StrinResponse getNumberOfMonth(){

        return new StrinResponse("Число месяцев: " +String.valueOf(settingsService.getNumberOfMonth()));
    }

    @PatchMapping(value="/change-number")
    public StrinResponse changeNumberOfMonth(@RequestParam("number") int number){
        //curl -X PATCH "http://localhost:8080/senla/settings/change-number?number=10"
        return new StrinResponse("Изменения: " +settingsService.changeNumberOfMonth(number));
    }

    @GetMapping(value = "/warehouse")
    public StrinResponse getWarehouseOption(){
        return  new StrinResponse("Значение: " + settingsService.getWarehouseOption());
    }

    @PatchMapping(value="/change-warehouse")
    public StrinResponse setWarehouseFunction(@RequestParam("func") String func){
        return new StrinResponse( "Изменения: " + settingsService.setWarehouseFunction(func));
    }


}
