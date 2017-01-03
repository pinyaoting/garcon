package com.pinyaoting.garcondecuisine.datastore;

import android.content.Context;
import android.os.Parcelable;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.interfaces.domain.DataStoreInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.utils.ConstantsAndUtils;
import com.pinyaoting.garcondecuisine.viewstates.Goal;
import com.pinyaoting.garcondecuisine.viewstates.GoalReducer;
import com.pinyaoting.garcondecuisine.viewstates.Idea;
import com.pinyaoting.garcondecuisine.viewstates.IdeaReducer;
import com.pinyaoting.garcondecuisine.models.Plan;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class DataStore implements DataStoreInterface {

    DataSnapshotStore mSnapshotStore;
    List<Observer<ViewState>> mIdeaStateObservers;
    List<Observer<ViewState>> mSuggestionStateObservers;
    List<Observer<ViewState>> mSavedGoalStateObservers;
    List<Observer<ViewState>> mGoalStateObservers;
    ViewState mIdeaState;
    ViewState mSuggestionState;
    ViewState mSavedGoalState;
    ViewState mGoalState;
    private Context mContext;
    private int mDisplayGoalFlag;

    public DataStore(Context context) {
        mSnapshotStore = new DataSnapshotStore();
        mIdeaStateObservers = new ArrayList<>();
        mSuggestionStateObservers = new ArrayList<>();
        mSavedGoalStateObservers = new ArrayList<>();
        mGoalStateObservers = new ArrayList<>();
        mIdeaState = new ViewState(R.id.state_idle);
        mSuggestionState = new ViewState(R.id.state_idle);
        mSavedGoalState = new ViewState(R.id.state_idle);
        mGoalState = new ViewState(R.id.state_idle);
        mDisplayGoalFlag = R.id.flag_explore_recipes;
        mContext = context;
    }

    @Override
    public void setIdeaState(ViewState state) {
        mIdeaState = state;
        notifyIdeaStateChange();
    }

    @Override
    public void setSuggestionState(ViewState state) {
        mSuggestionState = state;
        notifySuggestionStateChange();
    }

    @Override
    public void setGoalState(ViewState state) {
        mGoalState = state;
        notifyGoalStateChange();
    }

    @Override
    public void addIdea(Idea idea) {
        mSnapshotStore.addIdea(idea);
    }

    @Override
    public void removeIdea(int pos) {
        mSnapshotStore.removeIdea(pos);
    }

    @Override
    public void clearIdeas() {
        mSnapshotStore.clearIdeas();
    }

    @Override
    public int getIdeaCount() {
        return mSnapshotStore.getIdeas().size();
    }

    @Override
    public int getSuggestionCount() {
        return mSnapshotStore.getSuggestions().size();
    }

    @Override
    public void clearSuggestions() {
        mSnapshotStore.clearSuggestions();
    }

    @Override
    public void clearGoals() {
        mSnapshotStore.clearExploreGoals();
    }

    @Override
    public void subscribeToIdeaStateChanges(Observer<ViewState> observer) {
        mIdeaStateObservers.add(observer);
    }

    @Override
    public void subscribeToSuggestionStateChanges(Observer<ViewState> observer) {
        mSuggestionStateObservers.add(observer);
    }

    @Override
    public void subscribeToGoalStateChanges(Observer<ViewState> observer) {
        mGoalStateObservers.add(observer);
    }

    @Override
    public Idea getIdeaAtPos(int pos) {
        return mSnapshotStore.getIdeas().get(pos);
    }

    @Override
    public Idea getSuggestionAtPos(int pos) {
        return mSnapshotStore.getSuggestions().get(pos);
    }

    @Override
    public Plan getPlan() {
        return mSnapshotStore.getPlan();
    }

    @Override
    public void setPlan(Plan plan) {
        mSnapshotStore.setPlan(plan);
    }

    @Override
    public Plan createPlan(String id, String name) {
        Plan plan = new Plan(id, null, name, ConstantsAndUtils.getOwner(mContext));
        setPlan(plan);
        return plan;
    }

    private void notifyIdeaStateChange() {
        ConnectableObservable<ViewState> connectedObservable = Observable.just(
                mIdeaState).publish();
        for (Observer<ViewState> observer : mIdeaStateObservers) {
            connectedObservable.subscribeOn(Schedulers.immediate())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        connectedObservable.connect();
    }

    private void notifySuggestionStateChange() {
        ConnectableObservable<ViewState> connectedObservable = Observable.just(
                mSuggestionState).publish();
        for (Observer<ViewState> observer : mSuggestionStateObservers) {
            connectedObservable.subscribeOn(Schedulers.immediate())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        connectedObservable.connect();
    }

    private void notifyGoalStateChange() {
        ConnectableObservable<ViewState> connectedObservable = Observable.just(
                mGoalState).publish();
        for (Observer<ViewState> observer : mGoalStateObservers) {
            connectedObservable.subscribeOn(Schedulers.immediate())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        connectedObservable.connect();
    }

    @Override
    public void moveIdeaToBottom(int pos) {
        mSnapshotStore.moveIdeaToBottom(pos);
    }

    @Override
    public void moveIdeaToTop(int pos) {
        mSnapshotStore.moveIdeaToTop(pos);
    }

    @Override
    public void setSuggestions(List<Idea> ideas) {
        mSnapshotStore.setSuggestions(ideas);
    }

    @Override
    public void setExploreGoals(List<Goal> goals) {
        mSnapshotStore.setExploreGoals(goals);
    }

    @Override
    public void setSavedGoals(List<Goal> goals) {
        mSnapshotStore.setSavedGoals(goals);
    }

    @Override
    public IdeaReducer getIdeaReducer(String id) {
        return mSnapshotStore.getIdeaReducer(id);
    }

    @Override
    public GoalReducer getExploreGoalReducer(String id) {
        return mSnapshotStore.getExploreGoalReducer(id);
    }

    @Override
    public GoalReducer getSavedGoalReducer(String id) {
        return mSnapshotStore.getSavedGoalReducer(id);
    }

    @Override
    public Goal getGoalAtPos(int pos) {
        switch (mDisplayGoalFlag) {
            case R.id.flag_explore_recipes:
                return mSnapshotStore.getExploreGoals().get(pos);
            case R.id.flag_saved_recipes:
                return mSnapshotStore.getSavedGoals().get(pos);
        }
        return null;
    }

    @Override
    public int getGoalFlag() {
        return mDisplayGoalFlag;
    }

    @Override
    public void setGoalFlag(int flag) {
        mDisplayGoalFlag = flag;
    }

    @Override
    public int getGoalCount() {
        switch (mDisplayGoalFlag) {
            case R.id.flag_explore_recipes:
                return mSnapshotStore.getExploreGoals().size();
            case R.id.flag_saved_recipes:
                return mSnapshotStore.getSavedGoals().size();
        }
        return 0;
    }

    @Override
    public void clearPlan() {
        mSnapshotStore.clearPlan();
    }

    @Override
    public void addPendingIdeas(String id, List<Idea> pendingIdeas) {
        mSnapshotStore.addPendingIdeas(id, pendingIdeas);
    }

    @Override
    public void mergePendingIdeas(String id) {
        if (!mSnapshotStore.getPendingIdeas().containsKey(id)) {
            return;
        }
        Set<String> dup = new HashSet<>();
        Map<String, String> map = new HashMap<>();
        for (Idea idea : mSnapshotStore.getIdeas()) {
            dup.add(idea.getContent());
        }
        for (Idea idea : mSnapshotStore.getPendingIdeas().get(id)) {
            String content = idea.getContent();
            if (dup.contains(content)) {
                String ideaId = map.get(content);
                mSnapshotStore.getIdeaReducer(ideaId).increaseQuantity();
                continue;
            }
            mSnapshotStore.addIdea(idea);
            dup.add(idea.getContent());
            map.put(idea.getContent(), idea.getId());
        }
    }

    @Override
    public int getPendingIdeasCount(String id) {
        if (mSnapshotStore.getPendingIdeas().containsKey(id)) {
            List<Idea> ideas = mSnapshotStore.getPendingIdeas().get(id);
            if (ideas != null) {
                return ideas.size();
            }
        }
        return 0;
    }

    @Override
    public Idea getPendingIdea(String id, int pos) {
        if (mSnapshotStore.getPendingIdeas().containsKey(id)) {
            List<Idea> ideas = mSnapshotStore.getPendingIdeas().get(id);
            if (ideas.size() > pos) {
                return ideas.get(pos);
            }
        }
        return null;
    }

    @Override
    public void unsubscribeFromIdeaStateChanges(Observer<ViewState> observer) {
        mIdeaStateObservers.remove(observer);
    }

    @Override
    public void unsubscribeFromSuggestionStateChanges(Observer<ViewState> observer) {
        mSuggestionStateObservers.remove(observer);
    }

    @Override
    public void unsubscribeFromGoalStateChanges(Observer<ViewState> observer) {
        mGoalStateObservers.remove(observer);
    }

    @Override
    public Parcelable getDataSnapshot() {
        return Parcels.wrap(mSnapshotStore);
    }

    @Override
    public void restoreDataSnapshot(Parcelable snapshot) {
        mSnapshotStore = Parcels.unwrap(snapshot);
    }
}
