package com.pinyaoting.garcondecuisine.interfaces.domain;

import com.google.firebase.auth.FirebaseUser;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.viewstates.Goal;
import com.pinyaoting.garcondecuisine.viewstates.Idea;
import com.pinyaoting.garcondecuisine.models.Plan;

import rx.Observer;

public interface IdeaInteractorInterface {

    void acceptSuggestedIdeaAtPos(int pos);

    void crossoutIdea(int pos);

    void uncrossoutIdea(int pos);

    void removeIdea(int pos);

    void removeAllIdeas();

    void getSuggestions(String keyword);

    void subscribeIdeaStateChange(Observer<ViewState> observer);

    void subscribeSuggestionStateChange(Observer<ViewState> observer);

    int getIdeaCount();

    int getSuggestionCount();

    Idea getIdeaAtPos(int pos);

    Idea getSuggestionAtPos(int pos);

    Plan getPlan();

    Plan createPlan(String id, String name);

    void loadExternalPlan(String planId);

    void loadPlan(String planId, Observer<Plan> observer);

    void setPendingIdeas(Goal goal);

    void discardPlanIfEmpty();

    void clearPlan();

    int getPendingIdeasCount(String id);

    Idea getPendingIdea(String id, int pos);

    String myPlanId();

    void onSignedInInitialize(FirebaseUser user);

    void onSignedOutCleanup();

    void increaseQuantityAtPos(int pos);

    void decreaseQuantityAtPos(int pos);

}
