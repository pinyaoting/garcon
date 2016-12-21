package com.doublesp.garcon.interfaces.data;

import com.doublesp.garcon.models.v1.Recipe;

import java.util.List;

import rx.Observer;

public interface RecipeRepositoryInterface {

    void subscribe(Observer<List<Recipe>> observer);

    void searchRecipe(String keyword);

}
