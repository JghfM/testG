package com.example.author.service.impl;

import com.example.author.commons.I18Constants;
import com.example.author.entity.Quote;
import com.example.author.expection.ElementNotFoundException;
import com.example.author.expection.PageNumberAndSizeException;
import com.example.author.repository.QuoteRepository;
import com.example.author.service.QuoteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    @Autowired
    QuoteRepository quoteRepository;

    private final MessageProvider messageProvider;

    @Override
    public Quote findById(Long id) {
        return quoteRepository.findById(id).orElseThrow(()->
                new ElementNotFoundException(messageProvider.getLocalMessage(I18Constants.ITEM_NOT_FOUND.getKey(),
                        String.valueOf(id))));
    }

    @Override
    public Quote save(Quote quote) {
        return quoteRepository.save(quote);
    }

    @Override
    public List<Quote> findAll() {
        List<Quote> quotes = quoteRepository.findAll();
        return quotes;
    }

    @Override
    public Map<String, Object> findByQuoteAuthor(String quoteAuthor, int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0) {
            throw new PageNumberAndSizeException(messageProvider.getLocalMessage(I18Constants.PAGE_PARAMETER_BAD_REQUEST.getKey(),
                    String.valueOf(pageNumber),
                    String.valueOf(pageSize)));
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Quote> pageQuotes = quoteAuthor == null ? quoteRepository.findAll(pageable):
                quoteRepository.findByQuoteAuthorContainingIgnoreCase(quoteAuthor, pageable);

        List<Quote> quotes = pageQuotes.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("quotes", quotes);
        response.put("currentPage", pageQuotes.getNumber());
        response.put("totalItems", pageQuotes.getTotalElements());
        response.put("totalPages", pageQuotes.getTotalPages());
        response.put("pageSize", pageQuotes.getSize());
        return response;
    }
}
