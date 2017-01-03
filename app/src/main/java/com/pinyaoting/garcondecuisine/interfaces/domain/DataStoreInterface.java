package com.pinyaoting.garcondecuisine.interfaces.domain;

import android.os.Parcelable;

import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.models.Plan;
import com.pinyaoting.garcondecuisine.viewstates.Goal;
import com.pinyaoting.garcondecuisine.viewstates.GoalReducer;
import com.pinyaoting.garcondecuisine.viewstates.Idea;
import com.pinyaoting.garcondecuisine.viewstates.IdeaReducer;

import java.util.List;

import rx.Observer;

public interface DataStoreInterface {

    void setIdeaState(ViewState state);

    void setSuggestionState(ViewState state);

    void setGoalState(ViewState state);

    void addIdea(Idea idea);

    void moveIdeaToBottom(int pos);

    void moveIdeaToTop(int pos);

    void removeIdea(int pos);

    void clearIdeas();

    void setSuggestions(List<Idea> ideas);

    void setExploreGoals(List<Goal> goals);

    void setSavedGoals(List<Goal> goals);

    IdeaReducer getIdeaReducer(String id);

    GoalReducer getExploreGoalReducer(String id);

    GoalReducer getSavedGoalReducer(String id);

    Idea getIdeaAtPos(int pos);

    Idea getSuggestionAtPos(int pos);

    int getIdeaCount();

    int getSuggestionCount();

    void subscribeToIdeaStateChanges(Observer<ViewState> observer);

    void subscribeToSuggestionStateChanges(Observer<ViewState> observer);

    void subscribeToGoalStateChanges(Observer<ViewState> observer);

    void unsubscribeFromIdeaStateChanges(Observer<ViewState> observer);

    void unsubscribeFromSuggestionStateChanges(Observer<ViewState> observer);

    void unsubscribeFromGoalStateChanges(Observer<ViewState> observer);

    Plan getPlan();

    void setPlan(Plan plan);

    Plan createPlan(String id, String name);

    Goal getGoalAtPos(int pos);

    int getGoalCount();

    int getGoalFlag();

    void clearPlan();

    void setGoalFlag(int flag);

    void addPendingIdeas(String id, List<Idea> pendingIdeas);

    void mergePendingIdeas(String id);

    int getPendingIdeasCount(String id);

    Idea getPendingIdea(String id, int pos);

    Parcelable getDataSnapshot();

    void restoreDataSnapshot(Parcelable snapshot);
}
