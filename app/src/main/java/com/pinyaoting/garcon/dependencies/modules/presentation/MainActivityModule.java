package com.pinyaoting.garcon.dependencies.modules.presentation;

import android.support.v7.widget.RecyclerView;

import com.pinyaoting.garcon.actions.GoalActionHandler;
import com.pinyaoting.garcon.actions.GoalDetailActionHandler;
import com.pinyaoting.garcon.actions.IdeaListActionHandler;
import com.pinyaoting.garcon.activities.MainActivity;
import com.pinyaoting.garcon.adapters.GoalArrayAdapter;
import com.pinyaoting.garcon.adapters.IdeaListArrayAdapter;
import com.pinyaoting.garcon.adapters.SavedGoalArrayAdapter;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalDetailActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.IdeaListActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.scopes.PresentationLayerScope;

import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    private final MainActivity mActivity;
    private final int mCategory;

    public MainActivityModule(MainActivity activity, int category) {
        mActivity = activity;
        mCategory = category;
    }

    @Provides
    @PresentationLayerScope
    @Named("Composition")
    public RecyclerView.Adapter<RecyclerView.ViewHolder> providesIdeaListArrayAdapter(
            IdeaInteractorInterface ideaInteractor,
            IdeaListActionHandlerInterface ideaActionHandler) {
        return new IdeaListArrayAdapter(ideaInteractor, ideaActionHandler);
    }

    @Provides
    @PresentationLayerScope
    public IdeaListActionHandlerInterface providesListFragmentActionHandler(
            IdeaInteractorInterface ideaInteractor) {
        return new IdeaListActionHandler(mActivity, ideaInteractor);
    }

    @Provides
    @PresentationLayerScope
    public IdeaInteractorInterface providesIdeaInteractor(
            Map<Integer, IdeaInteractorInterface> ideaInteractors) {
        IdeaInteractorInterface ideaInteractor = ideaInteractors.get(mCategory);
        return ideaInteractor;
    }

    @Provides
    @PresentationLayerScope
    @Named("Goal")
    public RecyclerView.Adapter<RecyclerView.ViewHolder> providesGoalArrayAdapter(
            GoalInteractorInterface interactor, GoalActionHandlerInterface actionHandler) {
        return new GoalArrayAdapter(interactor, actionHandler);
    }

    @Provides
    @PresentationLayerScope
    @Named("SavedGoal")
    public RecyclerView.Adapter<RecyclerView.ViewHolder> providesSavedGoalArrayAdapter(
            GoalInteractorInterface interactor, GoalActionHandlerInterface actionHandler) {
        return new SavedGoalArrayAdapter(interactor, actionHandler);
    }

    @Provides
    @PresentationLayerScope
    public GoalActionHandlerInterface providesGoalActionHandler(
            GoalInteractorInterface interactor) {
        return new GoalActionHandler(mActivity, interactor);
    }

    @Provides
    @PresentationLayerScope
    public GoalDetailActionHandlerInterface providesGoalDetailActionHandler(
            GoalInteractorInterface interactor) {
        return new GoalDetailActionHandler(mActivity, interactor);
    }

}
