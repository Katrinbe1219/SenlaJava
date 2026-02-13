package com.example.application.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String start(){
        return "Book Store Api\n\n"+
                "ORDERS:\n "+
                "/orders - get all orders";
    }
}
