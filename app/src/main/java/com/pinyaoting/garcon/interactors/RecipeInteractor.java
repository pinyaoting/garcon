package com.pinyaoting.garcon.interactors;

import android.content.Context;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcon.interfaces.domain.DataStoreInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.models.v2.IngredientV2;
import com.pinyaoting.garcon.models.v2.RecipeV2;
import com.pinyaoting.garcon.models.v2.SavedRecipe;
import com.pinyaoting.garcon.viewstates.Goal;
import com.pinyaoting.garcon.viewstates.GoalReducer;
import com.pinyaoting.garcon.viewstates.Idea;
import com.pinyaoting.garcon.viewstates.IdeaMeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class RecipeInteractor implements GoalInteractorInterface {

    public static final int RECIPEV2_INTERACTOR_BATCH_SIZE = 10;
    public static final long RECIPEV2_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES = 500;
    public static final long RECIPEV2_BOOKMARK_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES = 2000;
    public static final int RECIPEV2_INTERACTOR_SEARCH_BY_INGREDIENT_RANKING_MORE_HITS = 1;
    public static final int RECIPEV2_INTERACTOR_SEARCH_BY_INGREDIENT_RANKING_FEWER_MISSES = 2;
    public static final boolean RECIPEV2_INTERACTOR_SEARCH_BY_INGREDIENT_SHOW_INGREDIENTS = true;
    public static final boolean RECIPEV2_INTERACTOR_SEARCH_BY_INGREDIENT_HIDE_INGREDIENTS = false;
    static final int count = 10;
    static final int offset = 0;

    Context mContext;
    DataStoreInterface mDataStore;
    RecipeRepositoryInterface mRecipeRepository;
    PublishSubject<String> mSearchDebouncer;
    PublishSubject<Integer> mBookmarkDebouncer;

    public RecipeInteractor(Context context, DataStoreInterface dataStore,
                              RecipeRepositoryInterface recipeRepository) {
        mContext = context;
        mDataStore = dataStore;
        mRecipeRepository = recipeRepository;
        mRecipeRepository.subscribe(new Observer<List<RecipeV2>>() {
            List<RecipeV2> mRecipes = new ArrayList<>();

            @Override
            public void onCompleted() {
                List<Goal> goals = new ArrayList<>();
                for (RecipeV2 recipe : mRecipes) {
                    boolean isBookmarked = (SavedRecipe.byId(recipe.getId()) != null);
                    String subTitle = null;
                    if (recipe.getReadyInMinutes() != null) {
                        subTitle = String.format(mContext.getString(R.string.subtitle_text),
                                recipe.getReadyInMinutes());
                    }
                    goals.add(new Goal(
                            recipe.getId(),
                            recipe.getTitle(),
                            subTitle,
                            recipe.getInstructions(),
                            recipe.getImage(),
                            isBookmarked
                    ));
                }
                mDataStore.setExploreGoals(goals);
                mDataStore.setGoalState(new ViewState(
                        R.id.state_loaded, ViewState.OPERATION.RELOAD));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<RecipeV2> recipes) {
                mRecipes.clear();
                mRecipes.addAll(recipes);
            }
        });
        mRecipeRepository.subscribeDetail(new Observer<RecipeV2>() {
            RecipeV2 mRecipe;

            @Override
            public void onCompleted() {
                GoalReducer exploreGoalReducer = mDataStore.getExploreGoalReducer(
                        mRecipe.getId());
                if (exploreGoalReducer != null) {
                    exploreGoalReducer.setDescription(mRecipe.getInstructions());
                }
                GoalReducer savedGoalReducer = mDataStore.getSavedGoalReducer(
                        mRecipe.getId());
                if (savedGoalReducer != null) {
                    savedGoalReducer.setDescription(mRecipe.getInstructions());
                }
                List<Idea> ideas = new ArrayList<>();
                Set<String> dedupSet = new HashSet<>();
                for (IngredientV2 ingredient : mRecipe.getExtendedIngredients()) {
                    if (dedupSet.contains(ingredient.getName())) {
                        continue;
                    }
                    Idea idea = new Idea(
                            ingredient.getId(),
                            R.id.idea_category_recipe_v2,
                            ingredient.getName(),
                            ingredient.getOriginalString(),
                            false,
                            1,
                            R.id.idea_type_user_generated,
                            new IdeaMeta(
                                    ingredient.getImage(),
                                    ingredient.getName(),
                                    ingredient.getOriginalString()));
                    ideas.add(idea);
                    dedupSet.add(ingredient.getName());
                }
                mDataStore.setPendingIdeas(mRecipe.getId(), ideas);
                mDataStore.setGoalState(new ViewState(
                        R.id.state_loaded, ViewState.OPERATION.UPDATE));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(RecipeV2 recipe) {
                mRecipe = recipe;
            }
        });
    }

    @Override
    public int getGoalCount() {
        return mDataStore.getGoalCount();
    }

    @Override
    public Goal getGoalAtPos(int pos) {
        return mDataStore.getGoalAtPos(pos);
    }

    @Override
    public void clearGoal() {
        mDataStore.clearGoals();
    }

    @Override
    public void bookmarkGoalAtPos(int pos) {
        mDataStore.setGoalState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.UPDATE, pos, 1));
        getBookmarkDebouncer().onNext(pos);
    }

    @Override
    public void search(String keyword) {
        mDataStore.setGoalState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.RELOAD));
        if (keyword == null) {
            mRecipeRepository.randomRecipe(RECIPEV2_INTERACTOR_BATCH_SIZE);
            return;
        }
        searchRecipeWithDebounce(keyword);
    }

    @Override
    public void searchGoalByIdeas(List<Idea> ideas) {
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (Idea idea : ideas) {
            ingredientsBuilder.append(idea.getContent());
            ingredientsBuilder.append(",");
        }
        ingredientsBuilder.deleteCharAt(ingredientsBuilder.lastIndexOf(","));
        mRecipeRepository.searchRecipeByIngredients(
                ingredientsBuilder.toString(),
                RECIPEV2_INTERACTOR_SEARCH_BY_INGREDIENT_HIDE_INGREDIENTS,
                RECIPEV2_INTERACTOR_BATCH_SIZE,
                RECIPEV2_INTERACTOR_SEARCH_BY_INGREDIENT_RANKING_FEWER_MISSES);
    }

    @Override
    public void loadDetailsForGoalAtPos(int pos) {
        mDataStore.setGoalState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.UPDATE));
        Goal goal = mDataStore.getGoalAtPos(pos);
        mRecipeRepository.searchRecipeDetail(goal.getId());
    }

    @Override
    public void subscribeToGoalStateChange(Observer<ViewState> observer) {
        mDataStore.subscribeToGoalStateChanges(observer);
    }

    private PublishSubject getDebouncer() {
        if (mSearchDebouncer == null) {
            mSearchDebouncer = PublishSubject.create();
            mSearchDebouncer.debounce(RECIPEV2_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES,
                    TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mRecipeRepository.searchRecipe(s, count, offset);
                        }
                    });
        }
        return mSearchDebouncer;
    }

    private PublishSubject getBookmarkDebouncer() {
        if (mBookmarkDebouncer == null) {
            mBookmarkDebouncer = PublishSubject.create();
            mBookmarkDebouncer.debounce(RECIPEV2_BOOKMARK_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES,
                    TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer pos) {
                            Goal goal = mDataStore.getGoalAtPos(pos);
                            SavedRecipe savedRecipe;
                            Long timestamp = Calendar.getInstance().getTimeInMillis();
                            if (!goal.isBookmarked()) {
                                savedRecipe = new SavedRecipe();
                                savedRecipe.setId(goal.getId());
                                savedRecipe.setTimestamp(timestamp);
                                savedRecipe.save();
                            } else {
                                savedRecipe = SavedRecipe.byId(goal.getId());
                                savedRecipe.setTimestamp(timestamp);
                            }
                            GoalReducer exploreGoalReducer = mDataStore.getExploreGoalReducer(
                                    goal.getId());
                            if (exploreGoalReducer != null) {
                                exploreGoalReducer.setBookmarked(true);
                            }
                            GoalReducer savedGoalReducer = mDataStore.getSavedGoalReducer(
                                    goal.getId());
                            if (savedGoalReducer != null) {
                                savedGoalReducer.setBookmarked(true);
                            }
                            mDataStore.setGoalState(new ViewState(
                                    R.id.state_loaded, ViewState.OPERATION.UPDATE, pos, 1));
                        }
                    });
        }
        return mBookmarkDebouncer;
    }

    private void searchRecipeWithDebounce(final String keyword) {
        getDebouncer().onNext(keyword);
    }

    @Override
    public int getDisplayGoalFlag() {
        return mDataStore.getGoalFlag();
    }

    @Override
    public void setDisplayGoalFlag(int flag) {
        mDataStore.setGoalState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.RELOAD));
        mDataStore.setGoalFlag(flag);
        switch (flag) {
            case R.id.flag_explore_recipes:
            case R.id.flag_saved_recipes:
                loadBookmarkedGoals();
                break;
        }
        mDataStore.setGoalState(new ViewState(
                R.id.state_loaded, ViewState.OPERATION.RELOAD));
    }

    private void loadBookmarkedGoals() {
        List<Goal> bookmarkedGoals = new ArrayList<>();
        List<RecipeV2> bookmarkedRecipes = SavedRecipe.savedRecipes();
        for (RecipeV2 recipe : bookmarkedRecipes) {
            String subTitle = null;
            if (recipe.getReadyInMinutes() != null) {
                subTitle = String.format(mContext.getString(R.string.subtitle_text),
                        recipe.getReadyInMinutes());
            }
            bookmarkedGoals.add(new Goal(
                    recipe.getId(),
                    recipe.getTitle(),
                    subTitle,
                    recipe.getInstructions(),
                    recipe.getImage(),
                    true));
        }
        mDataStore.setSavedGoals(bookmarkedGoals);
    }
}
