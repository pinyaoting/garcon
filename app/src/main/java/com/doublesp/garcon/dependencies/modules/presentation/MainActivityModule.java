package com.doublesp.garcon.dependencies.modules.presentation;

import android.support.v7.widget.RecyclerView;

import com.doublesp.garcon.actions.GoalActionHandler;
import com.doublesp.garcon.actions.GoalDetailActionHandler;
import com.doublesp.garcon.actions.ListFragmentActionHandler;
import com.doublesp.garcon.activities.MainActivity;
import com.doublesp.garcon.adapters.GoalArrayAdapter;
import com.doublesp.garcon.adapters.ListCompositionArrayAdapter;
import com.doublesp.garcon.interfaces.domain.IdeaInteractorInterface;
import com.doublesp.garcon.interfaces.presentation.GoalActionHandlerInterface;
import com.doublesp.garcon.interfaces.presentation.GoalDetailActionHandlerInterface;
import com.doublesp.garcon.interfaces.presentation.GoalInteractorInterface;
import com.doublesp.garcon.interfaces.presentation.ListFragmentActionHandlerInterface;
import com.doublesp.garcon.interfaces.presentation.SavedIdeasActionHandlerInterface;
import com.doublesp.garcon.interfaces.scopes.PresentationLayerScope;

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
    public RecyclerView.Adapter<RecyclerView.ViewHolder> providesListCompositionArrayAdapter(
            IdeaInteractorInterface ideaInteractor,
            ListFragmentActionHandlerInterface ideaActionHandler) {
        return new ListCompositionArrayAdapter(ideaInteractor, ideaActionHandler);
    }

    @Provides
    @PresentationLayerScope
    public ListFragmentActionHandlerInterface providesListFragmentActionHandler(
            IdeaInteractorInterface ideaInteractor) {
        return new ListFragmentActionHandler(mActivity, ideaInteractor);
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

    @Provides
    @PresentationLayerScope
    public SavedIdeasActionHandlerInterface providesSavedIdeasActionHandler() {
        return mActivity;
    }

}
