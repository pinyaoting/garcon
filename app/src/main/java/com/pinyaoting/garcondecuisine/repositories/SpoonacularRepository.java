package com.pinyaoting.garcondecuisine.repositories;

import android.app.Application;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.api.SpoonacularClient;
import com.pinyaoting.garcondecuisine.database.GarconDatabase;
import com.pinyaoting.garcondecuisine.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcondecuisine.models.Ingredient;
import com.pinyaoting.garcondecuisine.models.RandomRecipeResponse;
import com.pinyaoting.garcondecuisine.models.Recipe;
import com.pinyaoting.garcondecuisine.models.RecipeResponse;
import com.pinyaoting.garcondecuisine.models.Recipe_Ingredient;
import com.pinyaoting.garcondecuisine.utils.ImageUtils;
import com.pinyaoting.garcondecuisine.utils.NetworkUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class SpoonacularRepository implements RecipeRepositoryInterface {

    List<Observer<List<Recipe>>> mSubscribers;
    List<Observer<Recipe>> mDetailSubscribers;
    List<Observer<List<Recipe>>> mAutoCompleteRecipeSubscribers;
    List<Observer<List<Ingredient>>> mAutoCompleteIngredientSubscribers;
    private Application mApplication;
    private SpoonacularClient mClient;
    private Long mCacheTTL;

    public SpoonacularRepository(Application application, SpoonacularClient client) {
        mApplication = application;
        mClient = client;
        mSubscribers = new ArrayList<>();
        mDetailSubscribers = new ArrayList<>();
        mAutoCompleteRecipeSubscribers = new ArrayList<>();
        mAutoCompleteIngredientSubscribers = new ArrayList<>();
        mCacheTTL = (long)application.getResources().getInteger(R.integer.client_cache_ttl);
        getClient().subscribeRecipe(new Observer<RecipeResponse>() {
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
                String baseUri = recipeResponse.getBaseUri();
                List<Recipe> recipes = recipeResponse.getResults();
                for (Recipe recipe : recipes) {
                    recipe.setImage(ImageUtils.composeImageUri(baseUri, recipe.getImage()));
                }
                mRecipes.addAll(recipes);
                asyncPersistRecipes(mRecipes);
            }
        });
        getClient().subscribeRecipeByIngredients(new Observer<List<Recipe>>() {
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
            public void onNext(List<Recipe> recipes) {
                mRecipes.clear();
                mRecipes.addAll(recipes);
                asyncPersistRecipes(mRecipes);
            }
        });
        getClient().subscribeRandomRecipe(new Observer<RandomRecipeResponse>() {
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
            public void onNext(RandomRecipeResponse randomRecipeResponse) {
                mRecipes.clear();
                mRecipes.addAll(randomRecipeResponse.getRecipes());
                asyncPersistRecipes(mRecipes);
            }
        });
        getClient().subscribeRecipeDetail(new Observer<Recipe>() {
            Recipe mRecipe = null;

            @Override
            public void onCompleted() {
                notifyAllDetailObservers(mRecipe);
                List<Recipe> recipes = new ArrayList<>();
                recipes.add(mRecipe);
                asyncPersistRecipes(recipes);
            }

            @Override
            public void onError(Throwable e) {
                notifyAllDetailObservers(null);
            }

            @Override
            public void onNext(Recipe recipe) {
                mRecipe = recipe;
            }
        });
        getClient().subscribeAutoCompleteIngredient(new Observer<List<Ingredient>>() {
            List<Ingredient> mIngredients = new ArrayList<>();

            @Override
            public void onCompleted() {
                notifyAllAutoCompleteIngredientObservers(mIngredients);
            }

            @Override
            public void onError(Throwable e) {
                notifyAllAutoCompleteIngredientObservers(null);
            }

            @Override
            public void onNext(List<Ingredient> ingredients) {
                mIngredients.clear();
                mIngredients.addAll(ingredients);
            }
        });
        getClient().subscribeAutoCompleteRecipe(new Observer<List<Recipe>>() {
            List<Recipe> mRecipes = new ArrayList<>();

            @Override
            public void onCompleted() {
                notifyAllAutoCompleteRecipeObservers(mRecipes);
            }

            @Override
            public void onError(Throwable e) {
                notifyAllAutoCompleteRecipeObservers(null);
            }

            @Override
            public void onNext(List<Recipe> recipes) {
                mRecipes.clear();
                mRecipes.addAll(recipes);
            }
        });
    }

    protected SpoonacularClient getClient() {
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
    public void subscribeDetail(Observer<Recipe> observer) {
        mDetailSubscribers.add(observer);
    }

    @Override
    public void subscribeAutoCompleteIngredient(Observer<List<Ingredient>> observer) {
        mAutoCompleteIngredientSubscribers.add(observer);
    }

    @Override
    public void subscribeAutoCompleteRecipe(Observer<List<Recipe>> observer) {
        mAutoCompleteRecipeSubscribers.add(observer);
    }

    @Override
    public void searchRecipe(String keyword, int count, int offset) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }
        getClient().searchRecipe(keyword, count, offset);
    }

    @Override
    public void searchRecipeByIngredients(
            String ingredients,
            boolean fillIngredients,
            int number,
            int ranking) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }
        getClient().searchRecipeByIngredients(ingredients, fillIngredients, number, ranking);
    }

    @Override
    public void searchRecipeDetail(String id) {
        Recipe recipe = Recipe.byId(id);
        if (recipe != null && recipe.getExtendedIngredients() != null &&
                !recipe.getExtendedIngredients().isEmpty()) {
            Long lastAccessTime = recipe.getTimestamp();
            Long currentTime = Calendar.getInstance().getTimeInMillis();
            if (lastAccessTime != null && currentTime - lastAccessTime < mCacheTTL) {
                // load recipe from DataBase to prevent excessive network requests
                notifyAllDetailObservers(recipe);
                return;
            }
        }
        getClient().searchRecipeDetail(id);
    }

    @Override
    public void randomRecipe(int count) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            fallback();
            return;
        }
        getClient().randomRecipe(count);
    }

    @Override
    public void autoCompleteIngredients(String keyword, int count, boolean metaInformation) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }
        getClient().autoCompleteIngredient(keyword, count, metaInformation);
    }

    @Override
    public void autoCompleteRecipes(String keyword, int count) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }
        getClient().autoCompleteRecipe(keyword, count);
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

    void notifyAllDetailObservers(Recipe recipe) {
        ConnectableObservable<Recipe> connectableObservable = Observable.just(
                recipe).publish();
        for (Observer<Recipe> subscriber : mDetailSubscribers) {
            connectableObservable.subscribeOn(Schedulers.immediate()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    void notifyAllAutoCompleteIngredientObservers(List<Ingredient> ingredients) {
        ConnectableObservable<List<Ingredient>> connectableObservable = Observable.just(
                ingredients).publish();
        for (Observer<List<Ingredient>> subscriber : mAutoCompleteIngredientSubscribers) {
            connectableObservable.subscribeOn(Schedulers.immediate()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    void notifyAllAutoCompleteRecipeObservers(List<Recipe> recipes) {
        ConnectableObservable<List<Recipe>> connectableObservable = Observable.just(
                recipes).publish();
        for (Observer<List<Recipe>> subscriber : mAutoCompleteRecipeSubscribers) {
            connectableObservable.subscribeOn(Schedulers.immediate()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(subscriber);
        }
        connectableObservable.connect();
    }

    void asyncPersistRecipes(List<Recipe> recipes) {
        // persists into database in background thread
        FlowManager.getDatabase(GarconDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Recipe>() {
                            @Override
                            public void processModel(Recipe recipe) {
                                recipe.setTimestamp(Calendar.getInstance().getTimeInMillis());
                                recipe.save();
                                List<Ingredient> ingredients = recipe.getExtendedIngredients();
                                if (ingredients == null || ingredients.isEmpty()) {
                                    return;
                                }
                                List<Recipe_Ingredient> obsoleteRelations =
                                        Recipe_Ingredient.byRecipeId(recipe.getId());
                                for (Recipe_Ingredient obsoleteRelation : obsoleteRelations) {
                                    obsoleteRelation.delete();
                                }
                                for (Ingredient ingredient : ingredients) {
                                    ingredient.save();

                                    // save recipe-ingredients relation
                                    Recipe_Ingredient relation = new Recipe_Ingredient();
                                    relation.setRecipeId(recipe.getId());
                                    relation.setIngredient(ingredient);
                                    relation.save();
                                }
                            }
                        }).addAll(recipes).build())
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
}
