package com.pinyaoting.garcondecuisine.api;

import com.pinyaoting.garcondecuisine.interfaces.api.SpoonacularApiEndpointInterface;
import com.pinyaoting.garcondecuisine.models.Ingredient;
import com.pinyaoting.garcondecuisine.models.RandomRecipeResponse;
import com.pinyaoting.garcondecuisine.models.RecipeResponse;
import com.pinyaoting.garcondecuisine.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class SpoonacularClient {

    static final int DELAY_BETWEEN_API_CALLS = 1000;
    SpoonacularApiEndpointInterface apiService;
    List<Observer<RecipeResponse>> mSubscribers;
    List<Observer<Recipe>> mDetailSubscribers;
    List<Observer<List<Recipe>>> mRecipeSubscribers;
    List<Observer<RandomRecipeResponse>> mRandomRecipeSubscribers;
    List<Observer<List<Recipe>>> mAutoCompleteRecipeSubscribers;
    List<Observer<List<Ingredient>>> mAutoCompleteIngredientSubscribers;

    public SpoonacularClient(SpoonacularApiEndpointInterface apiService) {
        this.apiService = apiService;
        mSubscribers = new ArrayList<>();
        mRecipeSubscribers = new ArrayList<>();
        mRandomRecipeSubscribers = new ArrayList<>();
        mDetailSubscribers = new ArrayList<>();
        mAutoCompleteRecipeSubscribers = new ArrayList<>();
        mAutoCompleteIngredientSubscribers = new ArrayList<>();
    }

    public void searchRecipe(String keyword, int count, int offset) {
        Observable<RecipeResponse> call = apiService.searchRecipe(keyword, count, offset);
        ConnectableObservable connectableObservable = call.publish();
        connectableObservable.delay(DELAY_BETWEEN_API_CALLS, TimeUnit.MILLISECONDS);
        for (Observer<RecipeResponse> subscriber : mSubscribers) {
            connectableObservable.subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    public void searchRecipeDetail(String id) {
        Observable<Recipe> call = apiService.searchRecipeDetail(id);
        ConnectableObservable connectableObservable = call.publish();
        connectableObservable.delay(DELAY_BETWEEN_API_CALLS, TimeUnit.MILLISECONDS);
        for (Observer<Recipe> subscriber : mDetailSubscribers) {
            connectableObservable.subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    public void searchRecipeByIngredients(
            String ingredients,
            boolean fillIngredients,
            int number,
            int ranking) {
        Observable<List<Recipe>> call = apiService.searchRecipeByIngredients(
                ingredients,
                fillIngredients,
                number,
                ranking);
        ConnectableObservable connectableObservable = call.publish();
        connectableObservable.delay(DELAY_BETWEEN_API_CALLS, TimeUnit.MILLISECONDS);
        for (Observer<List<Recipe>> subscriber : mRecipeSubscribers) {
            connectableObservable.subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    public void randomRecipe(int count) {
        Observable<RandomRecipeResponse> call = apiService.randomRecipe(count);
        ConnectableObservable connectableObservable = call.publish();
        connectableObservable.delay(DELAY_BETWEEN_API_CALLS, TimeUnit.MILLISECONDS);
        for (Observer<RandomRecipeResponse> subscriber : mRandomRecipeSubscribers) {
            connectableObservable.subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    public void autoCompleteIngredient(String keyword, int number, boolean metaInformation) {
        Observable<List<Ingredient>> call = apiService.autocompleteIngredient(
                keyword, number, metaInformation);
        ConnectableObservable connectableObservable = call.publish();
        connectableObservable.delay(DELAY_BETWEEN_API_CALLS, TimeUnit.MILLISECONDS);
        for (Observer<List<Ingredient>> subscriber : mAutoCompleteIngredientSubscribers) {
            connectableObservable.subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    public void autoCompleteRecipe(String keyword, int number) {
        Observable<List<Recipe>> call = apiService.autocompleteRecipe(keyword, number);
        ConnectableObservable connectableObservable = call.publish();
        connectableObservable.delay(DELAY_BETWEEN_API_CALLS, TimeUnit.MILLISECONDS);
        for (Observer<List<Recipe>> subscriber : mAutoCompleteRecipeSubscribers) {
            connectableObservable.subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    public void subscribeRecipe(Observer<RecipeResponse> observer) {
        mSubscribers.add(observer);
    }

    public void subscribeRecipeDetail(Observer<Recipe> observer) {
        mDetailSubscribers.add(observer);
    }

    public void subscribeRecipeByIngredients(Observer<List<Recipe>> observer) {
        mRecipeSubscribers.add(observer);
    }

    public void subscribeRandomRecipe(Observer<RandomRecipeResponse> observer) {
        mRandomRecipeSubscribers.add(observer);
    }

    public void subscribeAutoCompleteIngredient(Observer<List<Ingredient>> observer) {
        mAutoCompleteIngredientSubscribers.add(observer);
    }

    public void subscribeAutoCompleteRecipe(Observer<List<Recipe>> observer) {
        mAutoCompleteRecipeSubscribers.add(observer);
    }
}
