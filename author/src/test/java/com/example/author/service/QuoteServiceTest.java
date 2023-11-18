package com.example.author.service;

import com.example.author.commons.I18Constants;
import com.example.author.entity.Quote;
import com.example.author.exception.ElementNotFoundException;
import com.example.author.repository.QuoteRepository;
import com.example.author.service.impl.MessageProvider;
import com.example.author.service.impl.QuoteServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private MessageProvider messageProvider;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    @Autowired
    ObjectMapper objectMapper;

    List<Quote> quotes;

    @BeforeAll
    public void setup() throws IOException {
        File file = new File("src/test/resources/quotes.json");
        quotes = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Quote.class));
    }

    @Test
    void checkQuotesHasBeenPopulated() {
       assertEquals(10, quotes.size());
    }

    @Test
    void testGetOneValidQuoteById() {
        when(quoteRepository.findById(5L)).thenReturn(Optional.of(quotes.get(4)));

        Quote quote = quoteService.findById(5L);
        assertEquals(quote.getQuoteText(),
                "Advice in old age is foolish for what can be more absurd than to increase our provisions for the road the nearer we approach to our journey's end.");
        assertEquals(quote.getQuoteAuthor(),
                "Marcus Tullius Cicero");
        assertEquals(quote.getQuoteGenre(), "age");
        assertEquals(quote.getVersion(), 0);
    }

    @Test
    void testGetOneInvalidQuoteById() {
        Long invalidId = 89L;

        when(messageProvider.getLocalMessage(I18Constants.ITEM_NOT_FOUND.getKey(),
                String.valueOf(invalidId))).thenReturn("Item with id " + invalidId + " found");

        var ex = new ElementNotFoundException(messageProvider.getLocalMessage(I18Constants.ITEM_NOT_FOUND.getKey(),
                String.valueOf(invalidId)));

        when(quoteRepository.findById(invalidId)).thenThrow(ex);

        var exception = assertThrows(ElementNotFoundException.class, () -> {
            quoteService.findById(invalidId);
        });

        assertEquals("Item with id 89 found", exception.getMessage());
    }

    @Test
    void testQuotesWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 4;

        var firstFourQuotes = quotes.stream().limit(pageSize).toList();

        Page<Quote> pagedResponse = new PageImpl<>(firstFourQuotes);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(quoteRepository.findAll(pageable)).thenReturn(pagedResponse);

        Map<String, Object> response = quoteService.findByQuoteAuthor(Optional.empty(), pageNumber,pageSize);
        assertEquals(response.get("quotes"), firstFourQuotes);
    }

    @Test
    void testQuotesWithFilter() {
        int pageNumber = 0;
        int pageSize = 4;
        var quoteAuthor = Optional.of("Marcus Tullius Cicero");


        var expectedQuotes = quotes.stream().filter(quote -> quote.getQuoteAuthor().equalsIgnoreCase(quoteAuthor.get())).limit(pageSize).toList();
        expectedQuotes.forEach(System.out::println);

        Page<Quote> pagedResponse = new PageImpl<>(expectedQuotes);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(quoteRepository.findByQuoteAuthorContainingIgnoreCase(quoteAuthor.get(), pageable)).thenReturn(pagedResponse);

        Map<String, Object> response = quoteService.findByQuoteAuthor(quoteAuthor, pageNumber,pageSize);
        assertEquals(response.get("quotes"), expectedQuotes);
    }

}
