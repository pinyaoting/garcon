package com.pinyaoting.garcon.interfaces.presentation;

import com.pinyaoting.garcon.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcon.fragments.GoalPreviewFragment;
import com.pinyaoting.garcon.fragments.GoalSearchFragment;
import com.pinyaoting.garcon.fragments.IdeaListFragment;
import com.pinyaoting.garcon.fragments.SavedGoalsFragment;

public interface InjectorInterface {

    void inject(IdeaListFragment fragment);

    void inject(GoalSearchFragment fragment);

    void inject(GoalPreviewFragment fragment);

    void inject(SavedGoalsFragment fragment);

    void inject(GoalDetailViewPagerFragment fragment);
}
