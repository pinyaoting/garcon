package com.pinyaoting.garcondecuisine.dependencies.components.presentation;

import com.pinyaoting.garcondecuisine.activities.MainActivity;
import com.pinyaoting.garcondecuisine.dependencies.modules.presentation.MainActivityModule;
import com.pinyaoting.garcondecuisine.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcondecuisine.fragments.GoalPreviewFragment;
import com.pinyaoting.garcondecuisine.fragments.GoalSearchFragment;
import com.pinyaoting.garcondecuisine.fragments.IdeaListFragment;
import com.pinyaoting.garcondecuisine.fragments.SavedGoalsFragment;
import com.pinyaoting.garcondecuisine.interfaces.scopes.PresentationLayerScope;

import dagger.Subcomponent;

@PresentationLayerScope
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivitySubComponent {

    void inject(IdeaListFragment fragment);

    void inject(GoalSearchFragment fragment);

    void inject(GoalPreviewFragment fragment);

    void inject(SavedGoalsFragment fragment);

    void inject(GoalDetailViewPagerFragment fragment);

    void inject(MainActivity activity);
}
