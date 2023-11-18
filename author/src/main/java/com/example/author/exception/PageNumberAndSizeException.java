package com.example.author.exception;

public class PageNumberAndSizeException extends RuntimeException {

    public PageNumberAndSizeException(String message){
        super(message);
    }

}