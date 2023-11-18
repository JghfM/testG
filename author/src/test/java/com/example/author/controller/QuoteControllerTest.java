package com.example.author.controller;

import com.example.author.commons.I18Constants;
import com.example.author.entity.Quote;
import com.example.author.exception.ElementNotFoundException;
import com.example.author.exception.PageNumberAndSizeException;
import com.example.author.service.QuoteService;
import com.example.author.service.impl.MessageProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(QuoteController.class)
public class QuoteControllerTest {

    @MockBean
    QuoteService quoteService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MessageProvider messageProvider;

    List<Quote> quotes;

    static final String BASE_URL = "/v1/api/quotes";

    @BeforeAll
    public void load() throws IOException {
        File file = new File("src/test/resources/quotes.json");
        quotes = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Quote.class));
        quotes.forEach(System.out::println);
    }

    @Test
    public void checkQuotesHasBeenPopulated() {
        assertEquals(10, quotes.size());
    }

    @Test
    public void givenExistingQuoteShouldBeAbleToRetrieveCorrectly() throws Exception {
        given(quoteService.findById(1L))
                .willReturn(quotes.get(0));
        mockMvc.perform(get(BASE_URL + "/1").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"quoteText\":\"Like everyone else who makes the mistake of getting older," +
                        " I begin each day with coffee and obituaries.\"" +
                        ",\"quoteAuthor\":\"Bill Cosby\",\"quoteGenre\":\"age\",\"version\":0}"));
    }

    @Test
    public void givenNonExistingQuoteShouldThrow404() throws Exception {
        Long invalidId = 87L;
        var ex = new ElementNotFoundException(messageProvider.getLocalMessage(I18Constants.ITEM_NOT_FOUND.getKey(),
                String.valueOf(invalidId)));
        given(quoteService.findById(1L))
                .willThrow(ex);
        mockMvc.perform(get(BASE_URL + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenQuotesOfAnAuthorShouldBeAbleToRetrieveCorrectly() throws Exception {
        int pageNumber = 0;
        int pageSize = 3;
        var quoteAuthor = Optional.of("Marcus Tullius Cicero");

        var authorQuotes = quotes.stream().filter(quote -> quote.getQuoteAuthor().equalsIgnoreCase(quoteAuthor.get())).toList();
        int totalElements = authorQuotes.size();
        authorQuotes = authorQuotes.stream().limit(pageSize).toList();


        Map<String, Object> response = new HashMap<>();
        response.put("quotes", authorQuotes);
        response.put("currentPage", pageNumber);
        response.put("totalItems", totalElements);
        response.put("pageSize", pageSize);


        given(quoteService.findByQuoteAuthor(quoteAuthor, pageNumber, pageSize))
                .willReturn(response);
        mockMvc.perform(get(BASE_URL + "?quoteAuthor=" + quoteAuthor.get() + "&pageSize=" + pageSize + " &pageNumber=" + pageNumber).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"totalItems\":4," +
                        "\"pageSize\":3," +
                        "\"currentPage\":0," +
                        "\"quotes\":" +
                        "[{\"id\":5,\"quoteText\":\"Advice in old age is foolish for what can be more absurd than to increase our provisions for the road the nearer we approach to our journey's end.\",\"quoteAuthor\":\"Marcus Tullius Cicero\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":8,\"quoteText\":\"Test 1.\",\"quoteAuthor\":\"Marcus Tullius Cicero\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":9,\"quoteText\":\"As I approve of a youth that has something of the old man in him, so I am no less pleased with an old man that has something of the youth. He that follows this rule may be old in body, but can never be so in mind.\",\"quoteAuthor\":\"Marcus tulLius CiCErO\",\"quoteGenre\":\"age\",\"version\":0}]}"));
    }

    @Test
    public void givenQuotesWhenRetrievingShouldReturn200() throws Exception {
        int pageNumber = 0;
        int pageSize = 6;
        int totalElements = quotes.size();

        var expectedQuotes = quotes.stream().limit(pageSize).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("quotes", expectedQuotes);
        response.put("currentPage", pageNumber);
        response.put("totalItems", totalElements);
        response.put("pageSize", pageSize);

        given(quoteService.findByQuoteAuthor(Optional.empty(), pageNumber, pageSize))
                .willReturn(response);
        mockMvc.perform(get(BASE_URL + "?pageSize=" + pageSize + " &pageNumber=" + pageNumber).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"totalItems\":10," +
                        "\"pageSize\":6," +
                        "\"currentPage\":0," +
                        "\"quotes\":" +
                        "[{\"id\":1,\"quoteText\":\"Like everyone else who makes the mistake of getting older, I begin each day with coffee and obituaries.\",\"quoteAuthor\":\"Bill Cosby\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":2,\"quoteText\":\"Age appears to be best in four things old wood best to burn, old wine to drink, old friends to trust, and old authors to read.\",\"quoteAuthor\":\"Francis Bacon\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":3,\"quoteText\":\"None are so old as those who have outlived enthusiasm.\",\"quoteAuthor\":\"Henry David Thoreau\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":4,\"quoteText\":\"I will never be an old man. To me, old age is always 15 years older than I am.\",\"quoteAuthor\":\"Francis Bacon\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":5,\"quoteText\":\"Advice in old age is foolish for what can be more absurd than to increase our provisions for the road the nearer we approach to our journey's end.\",\"quoteAuthor\":\"Marcus Tullius Cicero\",\"quoteGenre\":\"age\",\"version\":0}," +
                        "{\"id\":6,\"quoteText\":\"Forty is the old age of youth fifty the youth of old age.\",\"quoteAuthor\":\"Victor Hugo\",\"quoteGenre\":\"age\",\"version\":0}]}"));
    }

    @Test
    public void givenQuotesWithBadParametersWhenRetrievingShouldReturn400() throws Exception {
        int pageNumber = -20;
        int pageSize = -36;
        var ex = new PageNumberAndSizeException(messageProvider.getLocalMessage(I18Constants.PAGE_PARAMETER_BAD_REQUEST.getKey(),
                String.valueOf(pageNumber),
                String.valueOf(pageSize)));
        given(quoteService.findByQuoteAuthor(Optional.empty(), pageNumber, pageSize))
                .willThrow(ex);
        mockMvc.perform(get(BASE_URL + "?pageSize=" + pageSize + " &pageNumber=" + pageNumber).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
