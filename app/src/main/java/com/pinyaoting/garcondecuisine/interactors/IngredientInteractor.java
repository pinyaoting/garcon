package com.pinyaoting.garcondecuisine.interactors;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.domain.DataStoreInterface;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.models.Ingredient;
import com.pinyaoting.garcondecuisine.utils.ConstantsAndUtils;
import com.pinyaoting.garcondecuisine.viewstates.Goal;
import com.pinyaoting.garcondecuisine.viewstates.Idea;
import com.pinyaoting.garcondecuisine.viewstates.IdeaMeta;
import com.pinyaoting.garcondecuisine.viewstates.IdeaReducer;
import com.pinyaoting.garcondecuisine.models.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class IngredientInteractor implements IdeaInteractorInterface {

    public static final int INGREDIENT_INTERACTOR_BATCH_SIZE = 10;
    public static final long INGREDIENT_INTERACTOR_DEBOUNCE_TIME_IN_MILLIES = 500;

    Context mContext;
    DataStoreInterface mDataStore;
    RecipeRepositoryInterface mRecipeRepository;
    CloudRepositoryInterface mCloudRepository;
    PublishSubject<String> mSearchDebouncer;
    Goal mPendingGoal;

    public IngredientInteractor(Context context,
            DataStoreInterface ideaDataStore,
            RecipeRepositoryInterface recipeRepository,
            CloudRepositoryInterface cloudRepository) {
        mContext = context;
        mDataStore = ideaDataStore;
        mRecipeRepository = recipeRepository;
        mCloudRepository = cloudRepository;
        mRecipeRepository.subscribeAutoCompleteIngredient(new Observer<List<Ingredient>>() {
            List<Ingredient> mIngredients = new ArrayList<>();

            @Override
            public void onCompleted() {
                List<Idea> suggestions = new ArrayList<>();
                for (Ingredient ingredient : mIngredients) {
                    Idea idea = new Idea(ingredient.getId(),
                            R.id.idea_category_recipe,
                            ingredient.getName(),
                            ingredient.getOriginalString(),
                            false,
                            1,
                            R.id.idea_type_suggestion,
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
            public void onNext(List<Ingredient> ingredients) {
                mIngredients.clear();
                mIngredients.addAll(ingredients);
            }
        });
        mCloudRepository.subscribe(new Observer<DataSnapshot>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(DataSnapshot dataSnapshot) {
                Plan plan = dataSnapshot.getValue(Plan.class);
                mDataStore.setPlan(plan);
                mDataStore.setIdeaState(new ViewState(
                        R.id.state_loaded, ViewState.OPERATION.RELOAD));
            }
        });
    }

    @Override
    public void acceptSuggestedIdeaAtPos(int pos) {
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.ADD, mDataStore.getIdeaCount() - 1);
        mDataStore.setIdeaState(viewState);
        Idea idea = mDataStore.getSuggestionAtPos(pos);
        mDataStore.addIdea(idea);
        mCloudRepository.addNewItemToPlan(
                getPlan(), mDataStore.getIdeaCount() - 1);
    }

    @Override
    public void crossoutIdea(final int pos) {
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.UPDATE, pos);
        mDataStore.setIdeaState(viewState);
        Idea idea = mDataStore.getIdeaAtPos(pos);
        IdeaReducer reducer = mDataStore.getIdeaReducer(idea.getId());
        if (reducer != null) {
            reducer.setCrossedOut(true);
        }
        mCloudRepository.updateItemInPlan(mDataStore.getPlan(), pos);
    }

    @Override
    public void uncrossoutIdea(final int pos) {
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.UPDATE, pos);
        mDataStore.setIdeaState(viewState);
        Idea idea = mDataStore.getIdeaAtPos(pos);
        IdeaReducer reducer = mDataStore.getIdeaReducer(idea.getId());
        if (reducer != null) {
            reducer.setCrossedOut(false);
        }
        mCloudRepository.updateItemInPlan(mDataStore.getPlan(), pos);
    }

    @Override
    public void removeIdea(final int pos) {
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.REMOVE, pos);
        mDataStore.setIdeaState(viewState);
        mDataStore.removeIdea(pos);
        mCloudRepository.savePlan(getPlan());
    }

    @Override
    public void removeAllIdeas() {
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.RELOAD);
        mDataStore.setIdeaState(viewState);
        mDataStore.clearIdeas();
        mCloudRepository.savePlan(getPlan());
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
    public Plan createPlan(String id, String name) {
        Plan plan = mDataStore.createPlan(id, name);
        mCloudRepository.newPlan(plan);
        return plan;
    }

    @Override
    public void discardPlanIfEmpty() {
        Plan plan = getPlan();
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
    public void loadPlan(String planId, final Observer<Plan> observer) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing,
                ViewState.OPERATION.RELOAD));
        mCloudRepository.loadPlan(planId, new Observer<DataSnapshot>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(DataSnapshot dataSnapshot) {
                if (mPendingGoal != null) {
                    mDataStore.mergePendingIdeas(mPendingGoal.getId());
                    mCloudRepository.savePlan(getPlan());
                    mPendingGoal = null;
                }
                Observable.just(getPlan()).subscribe(observer);
            }
        });
    }

    @Override
    public void loadExternalPlan(String planId) {
        mDataStore.setIdeaState(new ViewState(
                R.id.state_refreshing,
                ViewState.OPERATION.RELOAD));
        mCloudRepository.loadPlan(planId, new Observer<DataSnapshot>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(DataSnapshot dataSnapshot) {
                subscribePlan();
            }
        });
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
    public void setPendingIdeas(Goal goal) {
        mPendingGoal = goal;
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

    public void subscribePlan() {
        String userEmail = ConstantsAndUtils.getOwner(mContext);
        mCloudRepository.share(getPlan(), userEmail);
    }

    @Override
    public String myPlanId() {
        return mCloudRepository.myPlanId();
    }

    @Override
    public void onSignedInInitialize(FirebaseUser user) {
        mCloudRepository.onSignedInInitialize(user);
        mCloudRepository.populateListId(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String planId) {
                createPlan(planId, ConstantsAndUtils.getDefaultTitle(getContext()));
            }
        });
    }

    @Override
    public void onSignedOutCleanup() {
        mCloudRepository.onSignedOutCleanup();
    }

    @Override
    public void increaseQuantityAtPos(int pos) {
        Idea idea = mDataStore.getIdeaAtPos(pos);
        IdeaReducer reducer = mDataStore.getIdeaReducer(idea.getId());
        if (reducer == null) {
            return;
        }
        reducer.setQuantity(idea.getQuantity() + 1);
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.UPDATE, pos);
        mDataStore.setIdeaState(viewState);
        mCloudRepository.updateItemInPlan(getPlan(), pos);
    }

    @Override
    public void decreaseQuantityAtPos(int pos) {
        Idea idea = mDataStore.getIdeaAtPos(pos);
        if (idea.getQuantity() < 1) {
            return;
        }
        IdeaReducer reducer = mDataStore.getIdeaReducer(idea.getId());
        if (reducer == null) {
            return;
        }
        reducer.setQuantity(idea.getQuantity() - 1);
        ViewState viewState = new ViewState(
                R.id.state_refreshing, ViewState.OPERATION.UPDATE, pos);
        mDataStore.setIdeaState(viewState);
        mCloudRepository.updateItemInPlan(getPlan(), pos);
    }
}
