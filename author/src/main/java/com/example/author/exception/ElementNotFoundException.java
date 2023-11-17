package com.example.author.expection;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException(String message){
        super(message);
    }

}