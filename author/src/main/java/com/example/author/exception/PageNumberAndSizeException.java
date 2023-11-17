package com.example.author.expection;

public class PageNumberAndSizeException extends RuntimeException {

    public PageNumberAndSizeException(String message){
        super(message);
    }

}