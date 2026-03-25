package com.example.application.controllers;

import com.example.application.AppConfig;
import com.example.application.config.TestConfig;
import com.example.application.dto.CreatedOrderDTO;
import com.example.application.dto.CustomerDTO;
import com.example.application.dto.OrderCreateDto;
import com.example.application.dto.OrderDTO;
import com.example.application.errors.GlobalExceptionController;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import com.example.application.security.SecurityConfiguration;
import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.Filter;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



//1) Зачем при ContextConfiguration ты еще добавляешь @WebAppConfiguration 2) ЧТо такое MOckito.reset(), до эьтого мы так не делали, кроме случая с SecurityontextHolder, почему где-то сбрасываем, где-то оставляем? 3)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SecurityConfiguration.class,
        TestConfig.class,
OrderController.class,
GlobalExceptionController.class})
@WebAppConfiguration
public class OrderControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    BookService bookService;

    @Autowired
    BookShopFacade bookshop;

    MockMvc mockMvc;
    @Autowired
    private OrderController orderController;






    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private List<Order> getListOrders(){
        Customer c = new Customer("jj", "asd", "as");
        return List.of(new Order(1,c), new Order(2,c));
    }

    private Order getOrderOne(){
        Customer c = new Customer("jj", "asd", "as");
        return new Order(1,c);
    }

    @Test
    @WithMockUser(username = "user_watcher", authorities = {"get_models", "ROLE_USER_WATCHER"})
    void getOrdersPositive()  throws Exception {
        when(bookshop.getSortedOrders(any(OrderSorting.class), any(Logger.class))).thenReturn(getListOrders());

        mockMvc.perform(get("/orders/")

                .param("type", "1")
                ).andExpect(status().isOk());
    }
    @Test
    @WithAnonymousUser
    void getOrdersNegative() throws Exception {

        when(bookshop.getSortedOrders(any(OrderSorting.class), any(Logger.class)))
                .thenReturn(getListOrders());

        mockMvc.perform(get("/orders/")
                        .param("type", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user_full", authorities = {"get_models", "ROLE_USER_FULL"})
    void getOrderPositive()  throws Exception {
        when(bookshop.getOrderById(anyInt(), any(Logger.class))).thenReturn( getOrderOne());

        mockMvc.perform(get("/orders/1")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user_full", authorities = {"get_models", "ROLE_USER_FULL"})
    void getOrderNegative()  throws Exception {
        // обрабатывается ошибка, которая прописана в контроллере
        when(bookshop.getOrderById(anyInt(), any(Logger.class))).thenThrow(NumberFormatException.class);

        mockMvc.perform(get("/orders/1")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user_full", authorities = {"delete_models", "ROLE_USER_FULL"})
    void deleteOrderPositive()  throws Exception {
        when(bookshop.getOrderById(anyInt(), any(Logger.class))).thenReturn( getOrderOne());
        when(bookshop.removeOrder(any(Order.class), any(Logger.class))).thenReturn(true);
        doNothing().when(bookService).cancellOrderRequests(any(Order.class), any(Logger.class));

        mockMvc.perform(delete("/orders/delete/1")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user_full", authorities = {"delete_models", "ROLE_USER_FULL"})
    void deleteOrderNegative()  throws Exception {
        // не нашлось заказа; не забудь подключить GloballExceptionController
        when(bookshop.getOrderById(anyInt(), any(Logger.class))).thenReturn( null);
        mockMvc.perform(delete("/orders/delete/1")
        ).andExpect(status().isInternalServerError());
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"get_settings", "ROLE_ADMIN"})
    void getSettingsPositive()  throws Exception {
        when(bookshop.getDoneOrdersInDiapazon(any(LocalDate.class), any(LocalDate.class),any(OrderSorting.class), any(Logger.class)))
                .thenReturn(getListOrders());

        mockMvc.perform(get("/orders/diapazon")
                .param("firstDate", "11-11-2025")
                .param("secondDate", "12-12-2026")
                .param("type", "title")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"get_models", "ROLE_ADMIN"})
    void getSettingsNegative()  throws Exception {
        // нет прав
        when(bookshop.getDoneOrdersInDiapazon(any(LocalDate.class), any(LocalDate.class),any(OrderSorting.class), any(Logger.class)))
                .thenReturn(getListOrders());

        mockMvc.perform(get("/orders/diapazon")
                .param("firstDate", "11-11-2025")
                .param("secondDate", "12-12-2026")
                .param("type", "title")
        ).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"get_settings", "ROLE_ADMIN"})
    void getOrderAmountPositive()  throws Exception {
        when(bookshop.getOrdersAmountInDiapazon(any(LocalDate.class), any(LocalDate.class), any(Logger.class)))
                .thenReturn(3);

        mockMvc.perform(get("/orders/amount")
                .param("firstDate", "11-11-2025")
                .param("secondDate", "12-12-2026")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"get_settings", "ROLE_ADMIN"})
    void getOrderAmountNegative()  throws Exception {
        // должна произойти обработка внутри контроллера
        when(bookshop.getOrdersAmountInDiapazon(any(LocalDate.class), any(LocalDate.class), any(Logger.class)))
                .thenThrow(DateTimeException.class);

        mockMvc.perform(get("/orders/amount")
                .param("firstDate", "11-11-2025")
                .param("secondDate", "12-12-2026")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_USER_WATCHER"})
    void getOrderIncomePositive()  throws Exception {
        // должна произойти обработка внутри контроллера
        when(bookshop.getIncomeInDiapazon(any(LocalDate.class), any(LocalDate.class), any(Logger.class)))
                .thenReturn(12D);

        mockMvc.perform(get("/orders/income")
                .param("firstDate", "11-11-2025")
                .param("secondDate", "12-12-2026")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_USER_WATCHER"})
    void getOrderIncomeNegative()  throws Exception {
        // должна произойти обработка внутри контроллера
        when(bookshop.getIncomeInDiapazon(any(LocalDate.class), any(LocalDate.class), any(Logger.class)))
                .thenThrow(DateTimeException.class);

        mockMvc.perform(get("/orders/income")
                .param("firstDate", "11-11-2025")
                .param("secondDate", "12-12-2026")
        ).andExpect(status().isOk());
    }

    private OrderCreateDto getCreateDto(){
        CustomerDTO c = new CustomerDTO();
        c.setEmail("as");
        c.setName("name");
        c.setSurname("surname");
        OrderCreateDto dto = new OrderCreateDto();
        dto.setCustomer(c);
        return dto;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"create_models","ROLE_USER_FULL"})
    void createOrderPositive()  throws Exception {
        // должна произойти обработка внутри контроллера
        ObjectMapper objectMapper = new ObjectMapper();

        when(bookService.getBooksByTitles(anyList(), any(Logger.class))).thenReturn(List.of());
        CreatedOrderDTO dto = new CreatedOrderDTO();
        dto.setStatus(OrderStatus.DONE);
        when(bookshop.createOrder(any(Order.class), any(Logger.class))).thenReturn(dto);
        doNothing().when(bookService).setLastPurchase(anyList(), any(Logger.class));

        mockMvc.perform(post("/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getCreateDto()))
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_USER_FULL"})
    void createOrderNegative()  throws Exception {
        // должна произойти обработка внутри контроллера
        ObjectMapper objectMapper = new ObjectMapper();

        when(bookService.getBooksByTitles(anyList(), any(Logger.class))).thenReturn(List.of());
        CreatedOrderDTO dto = new CreatedOrderDTO();
        dto.setStatus(OrderStatus.DONE);
        when(bookshop.createOrder(any(Order.class), any(Logger.class))).thenReturn(dto);
        doNothing().when(bookService).setLastPurchase(anyList(), any(Logger.class));

        mockMvc.perform(post("/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getCreateDto()))
        ).andExpect(status().isInternalServerError());
    }



}
