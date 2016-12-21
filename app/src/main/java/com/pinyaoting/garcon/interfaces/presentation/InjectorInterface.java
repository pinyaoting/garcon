package com.pinyaoting.garcon.interfaces.presentation;

import com.pinyaoting.garcon.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcon.fragments.GoalPreviewFragment;
import com.pinyaoting.garcon.fragments.GoalSearchFragment;
import com.pinyaoting.garcon.fragments.ListCompositionFragment;
import com.pinyaoting.garcon.fragments.SavedIdeasFragment;

public interface InjectorInterface {

    void inject(ListCompositionFragment fragment);

    void inject(GoalSearchFragment fragment);

    void inject(GoalPreviewFragment fragment);

    void inject(SavedIdeasFragment fragment);

    void inject(GoalDetailViewPagerFragment fragment);
}