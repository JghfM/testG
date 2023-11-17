package com.example.author.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quote")
@Data
public class Quote {
    @Id
    private Long id;
    private String quoteText;
    private String quoteAuthor;
    private String quoteGenre;
    private Long version;
}
