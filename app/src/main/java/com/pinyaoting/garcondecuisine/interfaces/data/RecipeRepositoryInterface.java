package com.pinyaoting.garcondecuisine.interfaces.data;

import com.pinyaoting.garcondecuisine.models.Ingredient;
import com.pinyaoting.garcondecuisine.models.Recipe;

import java.util.List;

import rx.Observer;

public interface RecipeRepositoryInterface {

    void subscribe(Observer<List<Recipe>> observer);

    void subscribeDetail(Observer<Recipe> observer);

    void subscribeAutoCompleteIngredient(Observer<List<Ingredient>> observer);

    void subscribeAutoCompleteRecipe(Observer<List<Recipe>> observer);

    void searchRecipe(String keyword, int count, int offset);

    void searchRecipeByIngredients(
            String ingredients,
            boolean fillIngredients,
            int number,
            int ranking);

    void searchRecipeDetail(String id);

    void autoCompleteIngredients(String keyword, int count, boolean metaInformation);

    void autoCompleteRecipes(String keyword, int count);

    void randomRecipe(int count);
}
