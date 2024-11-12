package com.example.construconecta_interdisciplinar_certo.models;

public class CardItem {
    private String title;
    private String description;

    public CardItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
