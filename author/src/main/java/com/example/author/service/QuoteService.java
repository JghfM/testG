package com.example.author.service;

import com.example.author.entity.Quote;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuoteService {

    public Quote findById(Long id);

    public Quote save(Quote quote);

    Map<String, Object> findByQuoteAuthor(Optional<String> quoteAuthor, int pageNumber, int pageSize);


}
