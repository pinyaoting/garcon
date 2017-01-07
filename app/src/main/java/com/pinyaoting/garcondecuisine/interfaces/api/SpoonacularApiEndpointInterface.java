package com.pinyaoting.garcondecuisine.interfaces.api;

import com.pinyaoting.garcondecuisine.models.Ingredient;
import com.pinyaoting.garcondecuisine.models.RandomRecipeResponse;
import com.pinyaoting.garcondecuisine.models.Recipe;
import com.pinyaoting.garcondecuisine.models.RecipeResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SpoonacularApiEndpointInterface {

    String REQUEST_HEADER_WITH_CACHE = "Cache-Control: max-age=640000";

    @Headers({REQUEST_HEADER_WITH_CACHE})
    @GET("recipes/search")
    Observable<RecipeResponse> searchRecipe(@Query("query") String keyword,
                                              @Query("number") int number,
                                              @Query("offset") int offset);

    @Headers({REQUEST_HEADER_WITH_CACHE})
    @GET("recipes/{id}/information")
    Observable<Recipe> searchRecipeDetail(@Path("id") String id);

    @Headers({REQUEST_HEADER_WITH_CACHE})
    @GET("recipes/random")
    Observable<RandomRecipeResponse> randomRecipe(@Query("number") int number);

    @Headers({REQUEST_HEADER_WITH_CACHE})
    @GET("food/ingredients/autocomplete")
    Observable<List<Ingredient>> autocompleteIngredient(
            @Query("query") String keyword,
            @Query("number") int number,
            @Query("metaInformation") boolean metaInformation);

    @Headers({REQUEST_HEADER_WITH_CACHE})
    @GET("recipes/autocomplete")
    Observable<List<Recipe>> autocompleteRecipe(
            @Query("query") String keyword,
            @Query("number") int number);

    @Headers({REQUEST_HEADER_WITH_CACHE})
    @GET("recipes/findByIngredients")
    Observable<List<Recipe>> searchRecipeByIngredients(
            @Query("ingredients") String ingredients,
            @Query("fillIngredients") boolean fillIngredients,
            @Query("number") int number,
            @Query("ranking") int ranking);
}
