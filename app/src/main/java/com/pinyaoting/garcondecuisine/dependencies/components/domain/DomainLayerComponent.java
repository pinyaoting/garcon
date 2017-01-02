package com.pinyaoting.garcondecuisine.dependencies.components.domain;

import com.pinyaoting.garcondecuisine.dependencies.components.data.DataLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.modules.domain.DomainLayerModule;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.scopes.DomainLayerScope;

import java.util.Map;

import dagger.Component;

@DomainLayerScope
@Component(dependencies = DataLayerComponent.class, modules = DomainLayerModule.class)
public interface DomainLayerComponent {

    Map<Integer, com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface>
    ideaInteractors();

    GoalInteractorInterface ideaSearchInteractor();

}
