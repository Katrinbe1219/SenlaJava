package com.example.application.exceptions;

public class OrderCanNotBeCreated extends RuntimeException {
    public OrderCanNotBeCreated(String message) {
        super(message);
    }
}
