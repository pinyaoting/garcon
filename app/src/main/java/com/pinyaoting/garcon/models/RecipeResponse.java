package com.pinyaoting.garcon.models;

import java.util.List;

public class RecipeResponse {

    List<Recipe> results;
    int number;
    int offset;
    int totalProducts;
    String baseUri;

    public List<Recipe> getResults() {
        return results;
    }

    public int getNumber() {
        return number;
    }

    public int getOffset() {
        return offset;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public String getBaseUri() {
        return baseUri;
    }
}
