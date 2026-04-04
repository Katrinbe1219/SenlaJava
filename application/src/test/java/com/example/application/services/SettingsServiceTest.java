package com.example.application.services;

import com.example.application.config.ServiceTestConfig;
import com.example.application.config.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceTestConfig.class})
public class SettingsServiceTest {

    @Autowired
    SettingsService settingsService;


    @Test
    @DisplayName("getNumberOfMonthIfCorrectValue")
    public void getNumberOfMonthIfCorrectValue(){
        int numberOfMonth = settingsService.getNumberOfMonth();
        Assertions.assertEquals(3, numberOfMonth);
    }

    @Test
    @DisplayName("getNumberOfMonthIfNotCorrectValue")
    public void getNumberOfMonthIfNotCorrectValue(){
        int numberOfMonth = settingsService.getNumberOfMonth();
        Assertions.assertNotEquals(3, numberOfMonth);
    }

    @Test
    @DisplayName("getFunctionIfCorrectValue")
    public void getFunctionIfCorrectValue(){
        String numberOfMonth = settingsService.getWarehouseOption();
        Assertions.assertEquals("true", numberOfMonth);
    }


    @Test
    @DisplayName("changeFunctionIfSuccessful")
    public void changeFunctionIfSuccessful(){
        String numberOfMonth = settingsService.setWarehouseFunction("true");
        Assertions.assertNull(numberOfMonth);
    }

    @Test
    @DisplayName("changeFunctionIfNotCorrectInput")
    public void changeFunctionIfNotCorrectInput(){
        String numberOfMonth = settingsService.setWarehouseFunction("hj");
        Assertions.assertEquals("Не правильно введены значения", numberOfMonth);
    }

    @Test
    @DisplayName("changeMonthIfSuccessful")
    public void changeMonthIfSuccessful(){
        String numberOfMonth = settingsService.changeNumberOfMonth(2);
        Assertions.assertEquals("set", numberOfMonth);
    }

}
