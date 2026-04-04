package com.example.application.controllers;

import com.example.application.config.TestConfig;
import com.example.application.errors.GlobalExceptionController;
import com.example.application.security.SecurityConfiguration;
import com.example.application.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TestConfig.class,
        SecurityConfiguration.class,
        SettingController.class,
        GlobalExceptionController.class
})
//@TestPropertySource(locations = "classpath:./config.properties") - не нужен, почему?
public class SettingControllerTest {

    @Autowired
    SettingsService settingsService;

    @InjectMocks
    SettingController settingController;

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", authorities ={"ROLE_ADMIN"} )
    @DisplayName("getNumberMonthIfRoleIsCorrect")
    public void getNumberMonthIfRoleIsCorrect() throws Exception {
        when(settingsService.getNumberOfMonth())
                .thenReturn(2);
        mockMvc.perform(get("/settings/month"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER_FULL"})
    @DisplayName("getNumberMonthIfRoleIsIncorrect")
    public void getNumberMonthIfRoleIsIncorrect() throws Exception {
        // не та роль
        when(settingsService.getNumberOfMonth())
                .thenReturn(2);
        mockMvc.perform(get("/settings/month"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities ={"ROLE_ADMIN"} )
    @DisplayName("getFunctionIfRoleIsCorrect")
    public void getFunctionIfRoleIsCorrect() throws Exception {
        when(settingsService.getWarehouseOption())
                .thenReturn("true");
        mockMvc.perform(get("/settings/month"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER_FULL"})
    @DisplayName("getFunctionIfRoleIsIncorrect")
    public void getFunctionIfRoleIsIncorrect() throws Exception {
        // не та роль
        when(settingsService.getWarehouseOption())
                .thenReturn("true");
        mockMvc.perform(get("/settings/warehouse"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    @DisplayName("changeFunctionIfAuthoritiesAre")
    public void changeFunctionIfAuthoritiesAre() throws Exception {
        when(settingsService.setWarehouseFunction(anyString())).thenReturn("true");
        mockMvc.perform(patch("/settings/change-warehouse")
                .param("func", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("changeFunctionIfAuthoritiesAreNot")
    public void changeFunctionIfAuthoritiesAreNot() throws Exception {
        when(settingsService.setWarehouseFunction(anyString())).thenReturn("true");
        mockMvc.perform(patch("/settings/change-warehouse")
                        .param("func", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    @DisplayName("changeNumberOfMonthPositiveIfParamIs")
    public void changeNumberOfMonthPositiveIfParamIs() throws Exception {
        when(settingsService.changeNumberOfMonth(anyInt())).thenReturn("true");
        mockMvc.perform(patch("/settings/change-number")
                        .param("number", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    @DisplayName("changeNumberOfMonthPositiveIfParamIsNot")
    public void changeNumberOfMonthPositiveIfParamIsNot() throws Exception {
        when(settingsService.changeNumberOfMonth(anyInt())).thenReturn("true");
        mockMvc.perform(patch("/settings/change-number")
                        )
                .andExpect(status().isInternalServerError());
    }
}
