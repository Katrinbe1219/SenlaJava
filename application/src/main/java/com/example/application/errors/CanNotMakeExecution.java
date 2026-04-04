package com.example.application.errors;

public class CanNotMakeExecution extends RuntimeException {
    public CanNotMakeExecution(String message) {
        super(message);
    }
}
