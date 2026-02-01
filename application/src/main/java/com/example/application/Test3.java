package com.example.application;

import com.example.application.controllers.*;
import com.example.application.dao.*;
import com.example.application.hibernate.BookHibImpl;
import com.example.application.hibernate.OrderHibImplementation;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.model.BookShop;
import com.example.application.model.Warehouse;
import com.example.application.repositories.BookRepository;
import com.example.application.repositories.OrderRepository;
import com.example.application.repositories.RequestRepository;
import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import com.example.application.services.OrderFileService;
import com.example.application.services.SettingsService;
import com.example.application.spring.AppConfig;
import com.example.processing_annotations.InjectAnnotationProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import java.sql.Connection;

public class Test3 {
    public static void main(String[] args)  {



        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);


            BookStoreController bc = (BookStoreController) context.getBean("bookStoreController");
            bc.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
