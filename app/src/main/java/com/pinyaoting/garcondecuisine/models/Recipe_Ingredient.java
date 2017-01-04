package com.pinyaoting.garcondecuisine.models;

import com.pinyaoting.garcondecuisine.database.GarconDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by pinyaoting on 1/3/17.
 */

@Table(database = GarconDatabase.class)
public class Recipe_Ingredient extends BaseModel {
    @PrimaryKey
    @Column
    String recipeId;

    @PrimaryKey
    @Column
    String ingredientId;

    public Recipe_Ingredient() {
        super();
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Ingredient getIngredient() {
        return Ingredient.byId(this.ingredientId);
    }
}
