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
import com.example.processing_annotations.InjectAnnotationProcessor;



import java.sql.Connection;

public class Test3 {
    public static void main(String[] args)  {
        // Creating system----------------------------------------
        // то что раньше делалось в конструкторе контроллера BookStoreSystem переходит сюда
        // кроме того, что реализовано с помощью паттерна фабрики


        // загрузка системы из бэкапа
//        final String FILENAME = "bookstore_system.dat";
//        BookStoreSystem bookStoreSystem = null;
//
//        if (BookStoreSystem.systemFileExists(FILENAME)){
//            try {
//                bookStoreSystem = BookStoreSystem.loadSystem(FILENAME);
//                System.out.println("Загрузка системы произошла успешно");
//            } catch (IOException | ClassNotFoundException e) {
//                System.out.println("Проблема при загрузке дерева: " + e.getMessage());
//                System.out.println("Будет создана новая система");
//            }
//        }else{
//            bookStoreSystem = new BookStoreSystem();
//            bookStoreSystem.initializeSystem(true);
//        }


        Warehouse warehouse = new Warehouse();
        BookShop bookshop =new BookShop();

        // начинается добавление зависимостей

        InjectAnnotationProcessor di = InjectAnnotationProcessor.getInstance();
        di.registerSingleton(Warehouse.class, warehouse);
        di.registerSingleton(BookShop.class, bookshop);
        //di.registerSingleton(BookStoreSystem.class, bookStoreSystem);
        // функция возвращает новый экзмепляр, а также сохраняет его у себя в di контейнере

        try{
            JDBCConnection idbc_ = di.getInstance(JDBCConnection.class);
            di.registerSingleton(Connection.class, idbc_.getConnection());

            di.getInstance(BookHibImpl.class);
            di.getInstance(RequestHibImpl.class);
            di.getInstance(OrderHibImplementation.class);


            di.getInstance(BookRepository.class);
            di.getInstance(OrderRepository.class);
            di.getInstance(RequestRepository.class);

            di.getInstance(BookService.class);
           di.getInstance(BookShopFacade.class);

            di.getInstance(SettingsService.class);
            di.getInstance(OrderFileService.class);

            di.getInstance(BookController.class);
            di.getInstance(OrderController.class);
           di.getInstance(RequestController.class);
            di.getInstance(SettingController.class);

        } catch (Exception e){
            System.out.println("Появилась проблема при попытке установления зависимостей " + e.getMessage());
        }




        try {
            BookStoreController bc = di.getInstance(BookStoreController.class);
            bc.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
