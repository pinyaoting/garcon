package com.doublesp.garcon.dependencies.components.data;

import com.doublesp.garcon.dependencies.components.application.ApplicationComponent;
import com.doublesp.garcon.dependencies.modules.data.DataLayerModule;
import com.doublesp.garcon.interfaces.data.RecipeRepositoryInterface;
import com.doublesp.garcon.interfaces.data.RecipeV2RepositoryInterface;
import com.doublesp.garcon.interfaces.scopes.DataLayerScope;

import dagger.Component;

@DataLayerScope
@Component(dependencies = {ApplicationComponent.class}, modules = DataLayerModule.class)
public interface DataLayerComponent {

    RecipeRepositoryInterface recipeRepository();

    RecipeV2RepositoryInterface recipeV2Repository();

}
