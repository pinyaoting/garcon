package com.pinyaoting.garcon.interfaces.domain;

import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.viewstates.Goal;
import com.pinyaoting.garcon.viewstates.Idea;
import com.pinyaoting.garcon.viewstates.Plan;
import com.pinyaoting.garcon.viewstates.User;

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

    void subscribePlan(final User currentUser);
}
