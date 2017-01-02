package com.pinyaoting.garcondecuisine.dependencies.components.presentation;

import com.pinyaoting.garcondecuisine.dependencies.components.domain.DomainLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.modules.presentation.MainActivityModule;
import com.pinyaoting.garcondecuisine.interfaces.scopes.PresentationLayerScope;

import dagger.Component;

@PresentationLayerScope
@Component(dependencies = DomainLayerComponent.class)
public interface PresentationLayerComponent {

    MainActivitySubComponent newMainActivitySubComponent(
            MainActivityModule activityModule);

}
