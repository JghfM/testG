package com.example.author.exception;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException(String message){
        super(message);
    }

}