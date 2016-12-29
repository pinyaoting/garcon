package com.pinyaoting.garcon.dependencies.components.presentation;

import com.pinyaoting.garcon.activities.MainActivity;
import com.pinyaoting.garcon.dependencies.modules.presentation.MainActivityModule;
import com.pinyaoting.garcon.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcon.fragments.GoalPreviewFragment;
import com.pinyaoting.garcon.fragments.GoalSearchFragment;
import com.pinyaoting.garcon.fragments.IdeaListFragment;
import com.pinyaoting.garcon.fragments.SavedGoalsFragment;
import com.pinyaoting.garcon.interfaces.scopes.PresentationLayerScope;

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
