package com.rafalcendrowski.AccountApplication.exceptions;

public class CustomNotFoundException extends RuntimeException{
    public CustomNotFoundException(String message) {
        super(message);
    }
}
