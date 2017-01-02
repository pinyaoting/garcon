package com.pinyaoting.garcondecuisine.dependencies.components.data;

import com.pinyaoting.garcondecuisine.dependencies.components.application.ApplicationComponent;
import com.pinyaoting.garcondecuisine.dependencies.modules.data.DataLayerModule;
import com.pinyaoting.garcondecuisine.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.scopes.DataLayerScope;

import dagger.Component;

@DataLayerScope
@Component(dependencies = {ApplicationComponent.class}, modules = DataLayerModule.class)
public interface DataLayerComponent {

    RecipeRepositoryInterface recipeV2Repository();
    CloudRepositoryInterface cloudRepository();

}
