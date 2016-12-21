package com.pinyaoting.garcon.repositories;

import android.app.Application;

import com.pinyaoting.garcon.api.EdamamClient;
import com.pinyaoting.garcon.database.RecipeDatabase;
import com.pinyaoting.garcon.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcon.models.v1.Ingredient;
import com.pinyaoting.garcon.models.v1.Recipe;
import com.pinyaoting.garcon.models.v1.RecipeResponse;
import com.pinyaoting.garcon.models.v1.RecipeResponseHit;
import com.pinyaoting.garcon.utils.NetworkUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class EdamamRepository implements RecipeRepositoryInterface {

    List<Observer<List<Recipe>>> mSubscribers;
    private Application mApplication;
    private EdamamClient mClient;


    public EdamamRepository(Application application, EdamamClient client) {
        mApplication = application;
        mClient = client;
        mSubscribers = new ArrayList<>();
        getClient().subscribe(new Observer<RecipeResponse>() {
            List<Recipe> mRecipes = new ArrayList<>();

            @Override
            public void onCompleted() {
                notifyAllObservers(mRecipes);
            }

            @Override
            public void onError(Throwable e) {
                notifyAllObservers(null);
            }

            @Override
            public void onNext(RecipeResponse recipeResponse) {
                mRecipes.clear();
                for (RecipeResponseHit hit : recipeResponse.getResponses()) {
                    mRecipes.add(hit.getRecipe());
                }
                // persists into database in background thread
                FlowManager.getDatabase(RecipeDatabase.class)
                        .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                                new ProcessModelTransaction.ProcessModel<Recipe>() {
                                    @Override
                                    public void processModel(Recipe recipe) {
                                        recipe.save();
                                        for (Ingredient ingredient : recipe.getIngredients()) {
                                            ingredient.setUri(recipe.getUri());
                                            ingredient.save();
                                        }
                                    }
                                }).addAll(mRecipes).build())
                        .error(new Transaction.Error() {
                            @Override
                            public void onError(Transaction transaction, Throwable error) {

                            }
                        })
                        .success(new Transaction.Success() {
                            @Override
                            public void onSuccess(Transaction transaction) {

                            }
                        }).build().execute();
            }
        });
    }

    protected EdamamClient getClient() {
        return mClient;
    }

    protected Application getApplication() {
        return mApplication;
    }

    @Override
    public void subscribe(Observer<List<Recipe>> observer) {
        mSubscribers.add(observer);
    }

    @Override
    public void searchRecipe(String keyword) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            fallback();
            return;
        }
        getClient().searchRecipe(keyword);
    }

    void fallback() {
        notifyAllObservers(Recipe.recentItems());
    }

    void notifyAllObservers(List<Recipe> recipes) {
        ConnectableObservable<List<Recipe>> connectableObservable = Observable.just(
                recipes).publish();
        for (Observer<List<Recipe>> subscriber : mSubscribers) {
            connectableObservable.subscribeOn(Schedulers.immediate()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

}
