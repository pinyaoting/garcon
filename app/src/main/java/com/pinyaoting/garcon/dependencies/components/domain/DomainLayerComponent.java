package com.pinyaoting.garcon.dependencies.components.domain;

import com.pinyaoting.garcon.dependencies.components.data.DataLayerComponent;
import com.pinyaoting.garcon.dependencies.modules.domain.DomainLayerModule;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.interfaces.scopes.DomainLayerScope;

import java.util.Map;

import dagger.Component;

@DomainLayerScope
@Component(dependencies = DataLayerComponent.class, modules = DomainLayerModule.class)
public interface DomainLayerComponent {

    Map<Integer, com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface>
    ideaInteractors();

    GoalInteractorInterface ideaSearchInteractor();

}
