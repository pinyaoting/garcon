package com.pinyaoting.garcon.models;

import com.pinyaoting.garcon.database.GarconDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@Table(database = GarconDatabase.class)
public class Recipe extends BaseModel {

    public List<Ingredient> extendedIngredients;
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
//        if (extendedIngredients == null || extendedIngredients.isEmpty()) {
//            extendedIngredients = SQLite.select()
//                    .from(Ingredient.class)
//                    .where(IngredientV2_Table.id.eq(id))
//                    .queryList();
//        }
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<Ingredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }
}
