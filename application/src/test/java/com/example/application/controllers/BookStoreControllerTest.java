package com.example.application.controllers;

import com.example.application.config.TestConfig;
import com.example.application.dto.ReceiveRequest;
import com.example.application.errors.GlobalExceptionController;
import com.example.application.model.types.OrderSorting;
import com.example.application.security.SecurityConfiguration;
import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import com.example.application.services.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TestConfig.class,
        SecurityConfiguration.class,
        GlobalExceptionController.class,
        BookStoreController.class
})
@WebAppConfiguration
public class BookStoreControllerTest {

    MockMvc mockMvc;
    @InjectMocks
    BookStoreController bookStoreController;

    @Autowired
    BookService bookService;

    @Autowired
    BookShopFacade bookshop;

    @Autowired
    SettingsService settingsService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(username ="admin" , authorities ={"ROLE_ADMIN"} )
    @DisplayName("receiveBookIfBookIsFound")
    public void receiveBookIfBookIsFound() throws Exception {

        ReceiveRequest req = new ReceiveRequest();
        ObjectMapper obj = new ObjectMapper();
        req.setBookName("k");
        when(bookService.receiveBook(anyString(), any(Logger.class))).thenReturn(true);
        when(bookshop.getSortedOrders(any(OrderSorting.class), any(Logger.class))).thenReturn(new ArrayList<>());
        when(settingsService.getWarehouseOption()).thenReturn("false");

        mockMvc.perform(patch("/receive")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(obj.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username ="admin" , authorities ={"ROLE_ADMIN"} )
    @DisplayName("receiveBookIfBookIsNotFound")
    public void receiveBookIfBookIsNotFound() throws Exception {

        ReceiveRequest req = new ReceiveRequest();
        ObjectMapper obj = new ObjectMapper();
        req.setBookName("k");
        // книга не найдена, но все отлажено
        when(bookService.receiveBook(anyString(), any(Logger.class))).thenReturn(false);


        mockMvc.perform(patch("/receive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(obj.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
