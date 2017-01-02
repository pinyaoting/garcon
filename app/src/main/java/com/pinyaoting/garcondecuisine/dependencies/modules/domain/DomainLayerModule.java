package com.pinyaoting.garcondecuisine.dependencies.modules.domain;

import android.content.Context;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.datastore.DataStore;
import com.pinyaoting.garcondecuisine.interactors.IngredientInteractor;
import com.pinyaoting.garcondecuisine.interactors.RecipeInteractor;
import com.pinyaoting.garcondecuisine.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.data.RecipeRepositoryInterface;
import com.pinyaoting.garcondecuisine.interfaces.domain.DataStoreInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.scopes.DomainLayerScope;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;

@Module
public class DomainLayerModule {

    private static Context mContext;

    public DomainLayerModule(Context context) {
        mContext = context;
    }

    @Provides
    @DomainLayerScope
    public DataStoreInterface providesIdeaDataStore() {
        return new DataStore(mContext);
    }

    @Provides
    @DomainLayerScope
    @IntoMap
    @IntKey(R.id.idea_category_recipe)
    public com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface
    providesRecipeIdeaInteractor(
            DataStoreInterface ideaDataStore,
            RecipeRepositoryInterface recipeRepository,
            CloudRepositoryInterface cloudRepository) {
        return new IngredientInteractor(mContext, ideaDataStore, recipeRepository, cloudRepository);
    }

    @Provides
    @DomainLayerScope
    public GoalInteractorInterface providesIdeaSearchInteractor(DataStoreInterface ideaDataStore,
                                                                RecipeRepositoryInterface recipeRepository) {
        return new RecipeInteractor(mContext, ideaDataStore, recipeRepository);
    }

}
