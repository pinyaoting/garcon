package com.pinyaoting.garcondecuisine.viewstates;

public class IdeaReducer {

    Idea mIdea;

    public IdeaReducer(Idea idea) {
        mIdea = idea;
    }

    public Idea getIdea() {
        return mIdea;
    }

    public void setId(String id) {
        mIdea.id = id;
    }

    public void setCategory(int category) {
        mIdea.category = category;
    }

    public void setContent(String content) {
        mIdea.content = content;
    }

    public void setDetail(String detail) {
        mIdea.detail = detail;
    }

    public void setCrossedOut(boolean crossedOut) {
        mIdea.crossedOut = crossedOut;
    }

    public void setQuantity(int quantity) {
        mIdea.quantity = quantity;
    }

    public void setType(int type) {
        mIdea.type = type;
    }

    private IdeaMeta getMeta() {
        if (mIdea.meta == null) {
            mIdea.meta = new IdeaMeta();
        }
        return mIdea.meta;
    }

    public void setImageUrl(String imageUrl) {
        getMeta().imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        getMeta().title = title;
    }

    public void setDescription(String description) {
        getMeta().description = description;
    }
}
