package com.example.author.service;

import com.example.author.entity.Quote;

import java.util.List;
import java.util.Map;

public interface QuoteService {

    public Quote findById(Long id);

    public Quote save(Quote quote);

    public List<Quote> findAll();

    Map<String, Object> findByQuoteAuthor(String quoteAuthor, int pageNumber, int pageSize);


}
