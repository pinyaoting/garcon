package com.doublesp.coherence.dependencies.components.presentation;

import com.doublesp.coherence.dependencies.components.domain.DomainLayerComponent;
import com.doublesp.coherence.dependencies.modules.presentation.HomeActivityModule;
import com.doublesp.coherence.interfaces.scopes.PresentationLayerScope;

import dagger.Component;

/**
 * Created by pinyaoting on 11/11/16.
 */

@PresentationLayerScope
@Component(dependencies = DomainLayerComponent.class)
public interface PresentationLayerComponent {

    HomeActivitySubComponent newListCompositionActivitySubComponent(
            HomeActivityModule activityModule);

}
