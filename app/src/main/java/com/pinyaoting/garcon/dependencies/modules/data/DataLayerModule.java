package com.pinyaoting.garcon.dependencies.modules.data;

import android.app.Application;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.api.EdamamClient;
import com.pinyaoting.garcon.api.SpoonacularClient;
import com.pinyaoting.garcon.interfaces.api.EdamamApiEndpointInterface;
import com.pinyaoting.garcon.interfaces.api.SpoonacularApiEndpointInterface;
import com.pinyaoting.garcon.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcon.interfaces.data.RecipeV2RepositoryInterface;
import com.pinyaoting.garcon.interfaces.scopes.DataLayerScope;
import com.pinyaoting.garcon.repositories.EdamamRepository;
import com.pinyaoting.garcon.repositories.SpoonacularRepository;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class DataLayerModule {

    @Provides
    @DataLayerScope
    public EdamamApiEndpointInterface providesRecipeApiEndpointInterface(
            Map<Integer, Retrofit> retrofitMap) {
        Retrofit retrofit = retrofitMap.get(R.id.idea_category_recipe);
        return retrofit.create(EdamamApiEndpointInterface.class);
    }

    @Provides
    @DataLayerScope
    public EdamamClient providesEdamamClient(EdamamApiEndpointInterface apiEndpointInterface) {
        return new EdamamClient(apiEndpointInterface);
    }

    @Provides
    @DataLayerScope
    public RecipeRepositoryInterface providesRecipeRepository(Application application,
                                                              EdamamClient client) {
        return new EdamamRepository(application, client);
    }

    @Provides
    @DataLayerScope
    public SpoonacularApiEndpointInterface providesRecipeV2ApiEndpointInterface(
            Map<Integer, Retrofit> retrofitMap) {
        Retrofit retrofit = retrofitMap.get(R.id.idea_category_recipe_v2);
        return retrofit.create(SpoonacularApiEndpointInterface.class);
    }

    @Provides
    @DataLayerScope
    public SpoonacularClient providesSpoonacularClient(
            SpoonacularApiEndpointInterface apiEndpointInterface) {
        return new SpoonacularClient(apiEndpointInterface);
    }

    @Provides
    @DataLayerScope
    public RecipeV2RepositoryInterface providesRecipeV2Repository(Application application,
                                                                  SpoonacularClient client) {
        return new SpoonacularRepository(application, client);
    }
}
