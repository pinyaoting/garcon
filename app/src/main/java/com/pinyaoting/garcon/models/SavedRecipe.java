package com.pinyaoting.garcon.models;

import com.pinyaoting.garcon.database.RecipeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@Table(database = RecipeDatabase.class)
public class SavedRecipe extends BaseModel {

    @PrimaryKey
    @Column
    String id;

    @Column
    Long timestamp;

    public SavedRecipe() {
        super();
    }

    public static SavedRecipe byId(String id) {
        return new Select().from(SavedRecipe.class).where(
                SavedRecipe_Table.id.eq(id)).querySingle();
    }

    public static List<Recipe> savedRecipes() {
        return new Select().from(Recipe.class).as("T")
                .join(SavedRecipe.class, Join.JoinType.INNER).as("U")
                .on(Recipe_Table.id.withTable(NameAlias.builder("T").build())
                        .eq(SavedRecipe_Table.id.withTable(NameAlias.builder("U").build())))
                .orderBy(SavedRecipe_Table.timestamp, false).queryList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
