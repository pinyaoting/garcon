package com.doublesp.garcon.interfaces.presentation;

import com.doublesp.garcon.fragments.GoalDetailViewPagerFragment;
import com.doublesp.garcon.fragments.GoalPreviewFragment;
import com.doublesp.garcon.fragments.GoalSearchFragment;
import com.doublesp.garcon.fragments.ListCompositionFragment;
import com.doublesp.garcon.fragments.SavedIdeasFragment;

public interface InjectorInterface {

    void inject(ListCompositionFragment fragment);

    void inject(GoalSearchFragment fragment);

    void inject(GoalPreviewFragment fragment);

    void inject(SavedIdeasFragment fragment);

    void inject(GoalDetailViewPagerFragment fragment);
}
