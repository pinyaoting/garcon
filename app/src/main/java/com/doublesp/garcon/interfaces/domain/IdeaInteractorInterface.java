package com.doublesp.garcon.interfaces.domain;

import com.doublesp.garcon.interfaces.presentation.ViewState;
import com.doublesp.garcon.viewmodels.Goal;
import com.doublesp.garcon.viewmodels.Idea;
import com.doublesp.garcon.viewmodels.Plan;

import rx.Observer;

public interface IdeaInteractorInterface {

    void acceptSuggestedIdeaAtPos(int pos);

    void crossoutIdea(int pos);

    void uncrossoutIdea(int pos);

    void removeIdea(int pos);

    void getSuggestions(String keyword);

    void subscribeIdeaStateChange(Observer<ViewState> observer);

    void subscribeSuggestionStateChange(Observer<ViewState> observer);

    int getIdeaCount();

    int getSuggestionCount();

    Idea getIdeaAtPos(int pos);

    Idea getSuggestionAtPos(int pos);

    Plan getPlan();

    void setPlan(Plan plan);

    Plan createPlan(String id, String name);

    void loadPendingIdeas(Goal goal);

    void discardPlanIfEmpty();

    void clearPlan();

    int getPendingIdeasCount(String id);

    Idea getPendingIdea(String id, int pos);
}
