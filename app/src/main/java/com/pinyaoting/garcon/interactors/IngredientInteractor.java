package com.pinyaoting.garcon.interactors;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcon.interfaces.data.RecipeV2RepositoryInterface;
import com.pinyaoting.garcon.interfaces.domain.DataStoreInterface;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.models.v2.IngredientV2;
import com.pinyaoting.garcon.utils.ConstantsAndUtils;
import com.pinyaoting.garcon.viewmodels.Goal;
import com.pinyaoting.garcon.viewmodels.Idea;
import com.pinyaoting.garcon.viewmodels.IdeaMeta;
import com.pinyaoting.garcon.viewmodels.IdeaReducer;
import com.pinyaoting.garcon.viewmodels.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class IngredientInteractor implements IdeaInteractorInterface {

    public static final int INGREDIENT_INTERACTOR_BATCH_SIZE = 10;
    public static final long INGREDIENT_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES = 500;

    DataStoreInterface mDataStore;
    RecipeV2RepositoryInterface mRecipeRepository;
    CloudRepositoryInterface mCloudRepository;
    PublishSubject<String> mSearchDebouncer;

    public IngredientInteractor(DataStoreInterface ideaDataStore,
                                RecipeV2RepositoryInterface recipeRepository,
            CloudRepositoryInterface cloudRepository) {
        mDataStore = ideaDataStore;
        mRecipeRepository = recipeRepository;
        mCloudRepository = cloudRepository;
        mRecipeRepository.subscribeAutoCompleteIngredient(new Observer<List<IngredientV2>>() {
            List<IngredientV2> mIngredients = new ArrayList<>();

            @Override
            public void onCompleted() {
                List<Idea> suggestions = new ArrayList<>();
                for (IngredientV2 ingredient : mIngredients) {
                    Idea idea = new Idea(ingredient.getId(),
                            R.id.idea_category_recipe_v2,
                            ingredient.getName(),
                            false, R.id.idea_type_suggestion,
                            new IdeaMeta(
                                    ConstantsAndUtils.getSpoonacularImageUrl(ingredient.getImage()),
                                    ingredient.getName(),
                                    null));
                    suggestions.add(idea);
                }
                mDataStore.setSuggestions(suggestions);
                mDataStore.setSuggestionState(new ViewState(
                        R.id.state_loaded, ViewState.OPERATION.RELOAD));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<IngredientV2> ingredientV2s) {
                mIngredients.clear();
                mIngredients.addAll(ingredientV2s);
            }
        });
    }

    @Override
    public void acceptSuggestedIdeaAtPos(int pos) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.ADD, mDataStore.getIdeaCount() - 1));
        Idea idea = mDataStore.getSuggestionAtPos(pos);
        mDataStore.addIdea(idea);
        mCloudRepository.addNewItemsToPlan(mDataStore.getPlan(),
                mDataStore.getIdeaCount() - 1, 1);
        mDataStore.setIdeaState(new ViewState(
                R.id.state_loaded, ViewState.OPERATION.ADD, mDataStore.getIdeaCount() - 1));
    }

    @Override
    public void crossoutIdea(int pos) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.REMOVE));
        Idea idea = mDataStore.getIdeaAtPos(pos);
        IdeaReducer reducer = mDataStore.getIdeaReducer(idea.getId());
        if (reducer != null) {
            reducer.setCrossedOut(true);
        }
        mDataStore.moveIdeaToBottom(pos);
        mCloudRepository.savePlan(mDataStore.getPlan());
        mDataStore.setIdeaState(new ViewState(
                R.id.state_loaded, ViewState.OPERATION.REMOVE));
    }

    @Override
    public void uncrossoutIdea(int pos) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.REMOVE));
        Idea idea = mDataStore.getIdeaAtPos(pos);
        IdeaReducer reducer = mDataStore.getIdeaReducer(idea.getId());
        if (reducer != null) {
            reducer.setCrossedOut(false);
        }
        mDataStore.moveIdeaToTop(pos);
        mCloudRepository.savePlan(mDataStore.getPlan());
        mDataStore.setIdeaState(new ViewState(
                R.id.state_loaded, ViewState.OPERATION.REMOVE));
    }

    @Override
    public void removeIdea(int pos) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.REMOVE, pos, 1));
        mDataStore.removeIdea(pos);
        mCloudRepository.savePlan(mDataStore.getPlan());
        mDataStore.setIdeaState(new ViewState(
                R.id.state_loaded, ViewState.OPERATION.REMOVE, pos, 1));
    }

    @Override
    public void subscribeIdeaStateChange(Observer<ViewState> observer) {
        mDataStore.subscribeToIdeaStateChanges(observer);
    }

    @Override
    public void subscribeSuggestionStateChange(Observer<ViewState> observer) {
        mDataStore.subscribeToSuggestionStateChanges(observer);
    }

    @Override
    public int getIdeaCount() {
        return mDataStore.getIdeaCount();
    }

    @Override
    public int getSuggestionCount() {
        return mDataStore.getSuggestionCount();
    }

    @Override
    public Idea getIdeaAtPos(int pos) {
        return mDataStore.getIdeaAtPos(pos);
    }

    @Override
    public Idea getSuggestionAtPos(int pos) {
        return mDataStore.getSuggestionAtPos(pos);
    }

    @Override
    public Plan getPlan() {
        return mDataStore.getPlan();
    }

    @Override
    public void setPlan(Plan plan) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing,
                ViewState.OPERATION.RELOAD));
        mDataStore.setPlan(plan);
        mCloudRepository.savePlan(mDataStore.getPlan());
        mDataStore.setIdeaState(new ViewState(
                R.id.state_loaded,
                ViewState.OPERATION.RELOAD));
    }

    @Override
    public Plan createPlan(String id, String name) {
        return mDataStore.createPlan(id, name);
    }

    @Override
    public void discardPlanIfEmpty() {
        Plan plan = mDataStore.getPlan();
        if (plan == null || plan.getId() == null) {
            return;
        }
        mCloudRepository.removePlan(plan);
    }

    @Override
    public void clearPlan() {
        mDataStore.clearPlan();
    }

    @Override
    public int getPendingIdeasCount(String id) {
        return mDataStore.getPendingIdeasCount(id);
    }

    @Override
    public Idea getPendingIdea(String id, int pos) {
        return mDataStore.getPendingIdea(id, pos);
    }

    @Override
    public void getSuggestions(String keyword) {
        if (keyword == null) {
            return;
        }
        mDataStore.setSuggestionState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.RELOAD));
        searchIngredientsWithDebounce(keyword);
    }

    @Override
    public void loadPendingIdeas(Goal goal) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.RELOAD));
        mDataStore.loadPendingIdeas(goal.getId());
        mDataStore.setIdeaState(new ViewState(
                R.id.state_loaded, ViewState.OPERATION.RELOAD));
    }

    private PublishSubject getDebouncer() {
        if (mSearchDebouncer == null) {
            mSearchDebouncer = PublishSubject.create();
            mSearchDebouncer.debounce(INGREDIENT_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES,
                    TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mRecipeRepository.autoCompleteIngredients(s,
                                    INGREDIENT_INTERACTOR_BATCH_SIZE, true);
                            mDataStore.setSuggestionState(new ViewState(
                                    R.id.state_loaded, ViewState.OPERATION.RELOAD));
                        }
                    });
        }
        return mSearchDebouncer;
    }

    private void searchIngredientsWithDebounce(final String keyword) {
        getDebouncer().onNext(keyword);
    }
}
