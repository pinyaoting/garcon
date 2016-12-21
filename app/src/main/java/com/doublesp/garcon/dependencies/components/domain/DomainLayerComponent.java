package com.doublesp.garcon.dependencies.components.domain;

import com.doublesp.garcon.dependencies.components.data.DataLayerComponent;
import com.doublesp.garcon.dependencies.modules.domain.DomainLayerModule;
import com.doublesp.garcon.interfaces.presentation.GoalInteractorInterface;
import com.doublesp.garcon.interfaces.scopes.DomainLayerScope;

import java.util.Map;

import dagger.Component;

@DomainLayerScope
@Component(dependencies = DataLayerComponent.class, modules = DomainLayerModule.class)
public interface DomainLayerComponent {

    Map<Integer, com.doublesp.garcon.interfaces.domain.IdeaInteractorInterface>
    ideaInteractors();

    GoalInteractorInterface ideaSearchInteractor();

}
