package com.pinyaoting.garcondecuisine.models;

import com.pinyaoting.garcondecuisine.database.GarconDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by pinyaoting on 1/3/17.
 */

@Table(database = GarconDatabase.class)
public class Recipe_Ingredient extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String recipeId;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Ingredient ingredient;

    public Recipe_Ingredient() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public static List<Recipe_Ingredient> byRecipeId(String recipeId) {
        return new Select().from(Recipe_Ingredient.class)
                .where(Recipe_Ingredient_Table.recipeId.eq(recipeId)).queryList();
    }
}
