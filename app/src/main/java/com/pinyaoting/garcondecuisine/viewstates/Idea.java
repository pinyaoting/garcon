package com.pinyaoting.garcondecuisine.viewstates;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class Idea {

    String id;
    int category;
    String content;
    String detail;
    boolean crossedOut;
    int quantity;
    int type; // R.id.idea_type_user_generated or R.id.idea_type_suggestion
    IdeaMeta meta;

    public Idea() {
    }

    public Idea(String id, int category, String content, String detail, boolean crossedOut,
            int quantity, int type, IdeaMeta meta) {
        this.id = id;
        this.category = category;
        this.content = content;
        this.detail = detail;
        this.crossedOut = crossedOut;
        this.quantity = quantity;
        this.type = type;
        this.meta = meta;
    }

    public String getId() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isCrossedOut() {
        return crossedOut;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getType() {
        return type;
    }

    public IdeaMeta getMeta() {
        return meta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Idea) {
            Idea target = (Idea) obj;
            return this.content.equals(target.content);
        }
        return false;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", getId());
        result.put("category", getCategory());
        result.put("content", getContent());
        result.put("detail", getDetail());
        result.put("crossedOut", isCrossedOut());
        result.put("quantity", getQuantity());
        result.put("type", getType());
        result.put("meta", getMeta());

        return result;
    }

}
