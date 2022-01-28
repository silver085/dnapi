package com.dn.DNApi.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("BuyItems")
public class BuyItem {
    @Id
    private String id;
    private String textBundle;
    private String textDescBundle;
    private Double price;
    private int Tokens;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextBundle() {
        return textBundle;
    }

    public void setTextBundle(String textBundle) {
        this.textBundle = textBundle;
    }

    public String getTextDescBundle() {
        return textDescBundle;
    }

    public void setTextDescBundle(String textDescBundle) {
        this.textDescBundle = textDescBundle;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getTokens() {
        return Tokens;
    }

    public void setTokens(int tokens) {
        Tokens = tokens;
    }
}
