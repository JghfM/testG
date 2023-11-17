package com.example.author.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageProvider {

    @Autowired
    private final MessageSource messageSource;

    public String getLocalMessage(String key, String... params){
        return messageSource.getMessage(key,
                params,
                Locale.ENGLISH);
    }
}
