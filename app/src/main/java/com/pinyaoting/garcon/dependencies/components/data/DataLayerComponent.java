package com.pinyaoting.garcon.dependencies.components.data;

import com.pinyaoting.garcon.dependencies.components.application.ApplicationComponent;
import com.pinyaoting.garcon.dependencies.modules.data.DataLayerModule;
import com.pinyaoting.garcon.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcon.interfaces.data.RecipeV2RepositoryInterface;
import com.pinyaoting.garcon.interfaces.scopes.DataLayerScope;

import dagger.Component;

@DataLayerScope
@Component(dependencies = {ApplicationComponent.class}, modules = DataLayerModule.class)
public interface DataLayerComponent {

    RecipeV2RepositoryInterface recipeV2Repository();
    CloudRepositoryInterface cloudRepository();

}
