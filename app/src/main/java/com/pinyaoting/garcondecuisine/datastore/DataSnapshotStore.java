package com.pinyaoting.garcondecuisine.datastore;

import com.pinyaoting.garcondecuisine.models.Plan;
import com.pinyaoting.garcondecuisine.viewstates.Goal;
import com.pinyaoting.garcondecuisine.viewstates.GoalReducer;
import com.pinyaoting.garcondecuisine.viewstates.Idea;
import com.pinyaoting.garcondecuisine.viewstates.IdeaReducer;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class DataSnapshotStore {

    private Plan mPlan;
    private Map<String, List<Idea>> mPendingIdeas;
    private List<Idea> mSuggestions;
    private List<Goal> mSavedGoals;
    private List<Goal> mExploreGoals;
    private Map<String, IdeaReducer> mIdeaReducers;
    private Map<String, GoalReducer> mExploreGoalReducers;
    private Map<String, GoalReducer> mSavedGoalReducers;

    public DataSnapshotStore() {
        mPendingIdeas = new HashMap<>();
        mSuggestions = new ArrayList<>();
        mSavedGoals = new ArrayList<>();
        mExploreGoals = new ArrayList<>();
        mIdeaReducers = new HashMap<>();
        mExploreGoalReducers = new HashMap<>();
        mSavedGoalReducers = new HashMap<>();
    }

    public Plan getPlan() {
        return mPlan;
    }

    public void setPlan(Plan plan) {
        mPlan = plan;
        mIdeaReducers.clear();
        List<Idea> ideas = plan.getIdeas();
        if (ideas != null) {
            for (Idea idea : ideas) {
                mIdeaReducers.put(idea.getId(), new IdeaReducer(idea));
            }
        }
    }

    public void clearPlan() {
        mPlan = null;
        mIdeaReducers.clear();
    }

    public List<Idea> getIdeas() {
        if (mPlan == null) {
            return null;
        }
        return mPlan.getIdeas();
    }

    public int getIdeaCount() {
        if (mPlan == null) {
            return 0;
        }
        return getIdeas().size();
    }

    public void addIdea(Idea idea) {
        if (mPlan == null) {
            return;
        }
        mIdeaReducers.put(idea.getId(), new IdeaReducer(idea));
        mPlan.getIdeas().add(idea);
    }

    public Idea removeIdea(int pos) {
        return getIdeas().remove(pos);
    }

    public void clearIdeas() {
        getIdeas().clear();
        mIdeaReducers.clear();
    }

    public void moveIdeaToBottom(int pos) {
        if (getIdeas().size() <= pos) {
            // out of bound
            return;
        }
        getIdeas().add(removeIdea(pos));
    }

    public void moveIdeaToTop(int pos) {
        if (getIdeas().size() <= pos) {
            // out of bound
            return;
        }
        getIdeas().add(0, removeIdea(pos));
    }

    public Map<String, List<Idea>> getPendingIdeas() {
        return mPendingIdeas;
    }

    public void addPendingIdeas(String id, List<Idea> pendingIdeas) {
        mPendingIdeas.put(id, pendingIdeas);
    }

    public List<Idea> getSuggestions() {
        return mSuggestions;
    }

    public void setSuggestions(List<Idea> suggestions) {
        mSuggestions.clear();
        mSuggestions.addAll(suggestions);
    }

    public List<Goal> getSavedGoals() {
        return mSavedGoals;
    }

    public void setSavedGoals(List<Goal> goals) {
        mSavedGoals.clear();
        mSavedGoalReducers.clear();
        for (Goal goal : goals) {
            mSavedGoalReducers.put(goal.getId(), new GoalReducer(goal));
        }
        mSavedGoals.addAll(goals);
    }

    public List<Goal> getExploreGoals() {
        return mExploreGoals;
    }

    public void setExploreGoals(List<Goal> goals) {
        mExploreGoals.clear();
        mExploreGoalReducers.clear();
        for (Goal goal : goals) {
            mExploreGoalReducers.put(goal.getId(), new GoalReducer(goal));
        }
        mExploreGoals.addAll(goals);
    }

    public IdeaReducer getIdeaReducer(String id) {
        return mIdeaReducers.get(id);
    }

    public GoalReducer getExploreGoalReducer(String id) {
        return mExploreGoalReducers.get(id);
    }

    public GoalReducer getSavedGoalReducer(String id) {
        return mSavedGoalReducers.get(id);
    }
}
