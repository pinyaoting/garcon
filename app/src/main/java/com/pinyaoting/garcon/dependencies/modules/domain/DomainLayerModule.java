package com.pinyaoting.garcon.dependencies.modules.domain;

import android.content.Context;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.datastore.DataStore;
import com.pinyaoting.garcon.interactors.IngredientInteractor;
import com.pinyaoting.garcon.interactors.RecipeV2Interactor;
import com.pinyaoting.garcon.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcon.interfaces.data.RecipeV2RepositoryInterface;
import com.pinyaoting.garcon.interfaces.domain.DataStoreInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.interfaces.scopes.DomainLayerScope;

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
    @IntKey(R.id.idea_category_recipe_v2)
    public com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface
    providesRecipeV2IdeaInteractor(
            DataStoreInterface ideaDataStore,
            RecipeV2RepositoryInterface recipeRepository,
            CloudRepositoryInterface cloudRepository) {
        return new IngredientInteractor(ideaDataStore, recipeRepository, cloudRepository);
    }

    @Provides
    @DomainLayerScope
    public GoalInteractorInterface providesIdeaSearchInteractor(DataStoreInterface ideaDataStore,
                                                                RecipeV2RepositoryInterface recipeRepository) {
        return new RecipeV2Interactor(mContext, ideaDataStore, recipeRepository);
    }

}
