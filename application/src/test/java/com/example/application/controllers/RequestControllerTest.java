package com.example.application.controllers;

import com.example.application.config.TestConfig;
import com.example.application.errors.GlobalExceptionController;
import com.example.application.model.RequestResult;
import com.example.application.model.types.RequestSorting;
import com.example.application.security.SecurityConfiguration;
import com.example.application.services.BookService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        TestConfig.class,
        RequestController.class,
        SecurityConfiguration.class,
        GlobalExceptionController.class
})
public class RequestControllerTest {

    @Autowired
    BookService bookService;


    MockMvc mockMvc;

    @InjectMocks
    RequestController controller;

    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    private List<RequestResult> getRequests(){
        List<RequestResult> requests = new ArrayList<>();
        requests.add(new RequestResult("Title", 1L));
        requests.add(new RequestResult("Title", 2L));
        return requests;
    }

    @Test
    @WithMockUser(username="user", authorities = {"ROLE_USER_WATCHER", "get_models"})
    void getAllReqPositive() throws Exception {
        when(bookService.getSortedRequests(any(RequestSorting.class), any(Logger.class)))
                .thenReturn(getRequests());
        mockMvc.perform(get("/requests")
                .param("type","1"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username="user", authorities = { "get_models"})
    void getAllReqNegative() throws Exception {
        // не хватает роли
        when(bookService.getSortedRequests(any(RequestSorting.class), any(Logger.class)))
                .thenReturn(getRequests());
        mockMvc.perform(get("/requests")
                        .param("type","1"))
                .andExpect(status().isForbidden());

    }
}
