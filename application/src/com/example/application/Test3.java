package com.example.application;

import com.example.application.controllers.BookStoreController;

public class Test3 {
    public static void main(String[] args) {
        // Creating system----------------------------------------


        try {
            BookStoreController bc = new BookStoreController();
            bc.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
