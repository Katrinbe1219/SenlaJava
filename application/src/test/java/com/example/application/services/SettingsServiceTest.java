package com.example.application.services;

import com.example.application.config.ServiceTestConfig;
import com.example.application.config.TestConfig;
import org.junit.jupiter.api.Assertions;
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
    public void getNumberOfMonthPositive(){
        int numberOfMonth = settingsService.getNumberOfMonth();
        Assertions.assertEquals(3, numberOfMonth);
    }

    @Test
    public void getNumberOfMonthNegative(){
        int numberOfMonth = settingsService.getNumberOfMonth();
        Assertions.assertNotEquals(3, numberOfMonth);
    }

    @Test
    public void getFunctionPositive(){
        String numberOfMonth = settingsService.getWarehouseOption();
        Assertions.assertEquals("true", numberOfMonth);
    }


    @Test
    public void changeFunctionPositive(){
        String numberOfMonth = settingsService.setWarehouseFunction("true");
        Assertions.assertNull(numberOfMonth);
    }

    @Test
    public void changeFunctionNegative(){
        String numberOfMonth = settingsService.setWarehouseFunction("hj");
        Assertions.assertEquals("Не правильно введены значения", numberOfMonth);
    }

    @Test
    public void changeMonthPositive(){
        String numberOfMonth = settingsService.changeNumberOfMonth(2);
        Assertions.assertEquals("set", numberOfMonth);
    }

}
