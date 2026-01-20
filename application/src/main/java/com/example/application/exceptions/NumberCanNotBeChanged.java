package com.example.application.exceptions;

public class NumberCanNotBeChanged extends RuntimeException {
    public NumberCanNotBeChanged(String message) {
        super(message);
    }
}
