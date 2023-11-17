package com.example.author.repository;

import com.example.author.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface QuoteRepository extends MongoRepository<Quote, Long> {

    Page<Quote> findByQuoteAuthorContainingIgnoreCase(String quoteAuthor, Pageable pageable);
}
