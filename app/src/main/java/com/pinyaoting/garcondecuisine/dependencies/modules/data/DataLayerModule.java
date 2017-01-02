package com.pinyaoting.garcondecuisine.dependencies.modules.data;

import android.app.Application;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.api.SpoonacularClient;
import com.pinyaoting.garcondecuisine.interfaces.api.SpoonacularApiEndpointInterface;
import com.pinyaoting.garcondecuisine.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.scopes.DataLayerScope;
import com.pinyaoting.garcondecuisine.repositories.FirebaseRepository;
import com.pinyaoting.garcondecuisine.repositories.SpoonacularRepository;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class DataLayerModule {

    @Provides
    @DataLayerScope
    public SpoonacularApiEndpointInterface providesRecipeApiEndpointInterface(
            Map<Integer, Retrofit> retrofitMap) {
        Retrofit retrofit = retrofitMap.get(R.id.idea_category_recipe);
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
    public RecipeRepositoryInterface providesRecipeRepository(Application application,
                                                                  SpoonacularClient client) {
        return new SpoonacularRepository(application, client);
    }

    @Provides
    @DataLayerScope
    public CloudRepositoryInterface providesCloudRepository() {
        return new FirebaseRepository();
    }
}
