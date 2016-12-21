package com.doublesp.garcon.dependencies.components.presentation;

import com.doublesp.garcon.activities.MainActivity;
import com.doublesp.garcon.dependencies.modules.presentation.MainActivityModule;
import com.doublesp.garcon.fragments.GoalDetailViewPagerFragment;
import com.doublesp.garcon.fragments.GoalPreviewFragment;
import com.doublesp.garcon.fragments.GoalSearchFragment;
import com.doublesp.garcon.fragments.ListCompositionFragment;
import com.doublesp.garcon.fragments.SavedIdeasFragment;
import com.doublesp.garcon.interfaces.scopes.PresentationLayerScope;

import dagger.Subcomponent;

@PresentationLayerScope
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivitySubComponent {

    void inject(ListCompositionFragment fragment);

    void inject(GoalSearchFragment fragment);

    void inject(GoalPreviewFragment fragment);

    void inject(SavedIdeasFragment fragment);

    void inject(GoalDetailViewPagerFragment fragment);

    void inject(MainActivity activity);
}
