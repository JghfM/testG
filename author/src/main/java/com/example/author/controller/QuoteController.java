package com.example.author.controller;

import com.example.author.entity.Quote;
import com.example.author.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/quotes")
public class QuoteController {

    @Autowired
    QuoteService quoteService;

    Logger logger = Logger.getLogger(QuoteController.class.getName());

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable("id") Long id) {
        Quote result = quoteService.findById(id);
        logger.info("The result is: " + result);
        return result == null ? ResponseEntity.notFound().build():ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<Quote> save(@RequestBody Quote quote) {
        Quote result = quoteService.save(quote);
        logger.info("The request body is: " + result);
        return result == null ? ResponseEntity.badRequest().build():ResponseEntity.ok(result);
    }

    @GetMapping("/getAll")
    public  ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String quoteAuthor,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {
        logger.info("Inside getAll() ");

        Map<String, Object> response = quoteService.findByQuoteAuthor(quoteAuthor, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }
}
