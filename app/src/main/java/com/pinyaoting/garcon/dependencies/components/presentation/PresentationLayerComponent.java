package com.pinyaoting.garcon.dependencies.components.presentation;

import com.pinyaoting.garcon.dependencies.components.domain.DomainLayerComponent;
import com.pinyaoting.garcon.dependencies.modules.presentation.MainActivityModule;
import com.pinyaoting.garcon.interfaces.scopes.PresentationLayerScope;

import dagger.Component;

@PresentationLayerScope
@Component(dependencies = DomainLayerComponent.class)
public interface PresentationLayerComponent {

    MainActivitySubComponent newListCompositionActivitySubComponent(
            MainActivityModule activityModule);

}
