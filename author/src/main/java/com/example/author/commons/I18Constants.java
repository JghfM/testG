package com.example.author.commons;

import lombok.Getter;

@Getter
public enum I18Constants {

    ITEM_NOT_FOUND("item.absent"),
    LIST_NOT_FOUND("list.absent"),
    PAGE_PARAMETER_BAD_REQUEST("page.parameters");

    private final String key;
    I18Constants(String key) {
        this.key = key;
    }

}