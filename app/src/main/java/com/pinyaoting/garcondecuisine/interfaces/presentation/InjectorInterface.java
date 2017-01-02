package com.pinyaoting.garcondecuisine.interfaces.presentation;

import com.pinyaoting.garcondecuisine.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcondecuisine.fragments.GoalPreviewFragment;
import com.pinyaoting.garcondecuisine.fragments.GoalSearchFragment;
import com.pinyaoting.garcondecuisine.fragments.IdeaListFragment;
import com.pinyaoting.garcondecuisine.fragments.SavedGoalsFragment;

public interface InjectorInterface {

    void inject(IdeaListFragment fragment);

    void inject(GoalSearchFragment fragment);

    void inject(GoalPreviewFragment fragment);

    void inject(SavedGoalsFragment fragment);

    void inject(GoalDetailViewPagerFragment fragment);
}
