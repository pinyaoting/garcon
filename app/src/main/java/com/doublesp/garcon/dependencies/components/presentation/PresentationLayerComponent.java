package com.doublesp.garcon.dependencies.components.presentation;

import com.doublesp.garcon.dependencies.components.domain.DomainLayerComponent;
import com.doublesp.garcon.dependencies.modules.presentation.MainActivityModule;
import com.doublesp.garcon.interfaces.scopes.PresentationLayerScope;

import dagger.Component;

@PresentationLayerScope
@Component(dependencies = DomainLayerComponent.class)
public interface PresentationLayerComponent {

    MainActivitySubComponent newListCompositionActivitySubComponent(
            MainActivityModule activityModule);

}
