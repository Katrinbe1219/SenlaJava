package task_3_4.services;

import task_3_4.exceptions.NumberCanNotBeChanged;
import task_3_4.repositories.PropertiesRepository;

public class SettingsService {
    private PropertiesRepository repo;

    public SettingsService(PropertiesRepository repo) {
        this.repo = repo;
    }

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
