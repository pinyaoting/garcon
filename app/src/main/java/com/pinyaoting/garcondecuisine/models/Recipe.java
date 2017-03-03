package com.pinyaoting.garcondecuisine.models;

import android.net.Uri;

import com.pinyaoting.garcondecuisine.database.GarconDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.ArrayList;
import java.util.List;

@TableEndpoint(name = Recipe.NAME, contentProvider = GarconDatabase.class)
@Table(database = GarconDatabase.class, name = Recipe.NAME)
public class Recipe extends BaseProviderModel<Recipe> {

    public static final String NAME = "Recipe";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(
            GarconDatabase.AUTHORITY);

    @PrimaryKey
    @Column
    String id;
    @Column
    String title;
    @Column
    Long readyInMinutes;
    @Column
    String image;
    @Column
    String instructions;
    @Column
    Long timestamp;

    public List<Ingredient> extendedIngredients;

    public Recipe() {
        super();
    }

    public static Recipe byId(String id) {
        return new Select().from(Recipe.class).where(Recipe_Table.id.eq(id)).querySingle();
    }

    public static List<Recipe> recentItems() {
        return new Select().from(Recipe.class).limit(50).queryList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Long readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "extendedIngredients")
    public List<Ingredient> getExtendedIngredients() {
        if (extendedIngredients == null || extendedIngredients.isEmpty()) {
            List<Recipe_Ingredient> list = new Select()
                    .from(Recipe_Ingredient.class)
                    .where(Recipe_Ingredient_Table.recipeId.eq(id))
                    .queryList();
            extendedIngredients = new ArrayList<>();
            for (Recipe_Ingredient item: list) {
                extendedIngredients.add(item.getIngredient());
            }
        }
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<Ingredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Uri getDeleteUri() {
        return Recipe.CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return Recipe.CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return Recipe.CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return Recipe.CONTENT_URI;
    }
}
