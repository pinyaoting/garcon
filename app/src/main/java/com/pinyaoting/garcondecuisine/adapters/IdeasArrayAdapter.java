package com.pinyaoting.garcondecuisine.adapters;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.viewholders.IdeaDetailViewHolder;
import com.pinyaoting.garcondecuisine.viewstates.Idea;

/**
 * Created by pinyaoting on 12/6/16.
 */

public class IdeasArrayAdapter extends RecyclerView.Adapter {

    IdeaInteractorInterface mIdeaInteractor;
    String mGoalId;

    public IdeasArrayAdapter(IdeaInteractorInterface ideaInteractor, String goalId) {
        mIdeaInteractor = ideaInteractor;
        mGoalId = goalId;
    }

    public void setGoalId(String goalId) {
        this.mGoalId = goalId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_idea_detail, parent, false);
        return new IdeaDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Idea viewState = mIdeaInteractor.getPendingIdea(mGoalId, position);
        if (holder instanceof IdeaDetailViewHolder) {
            IdeaDetailViewHolder viewHolder = (IdeaDetailViewHolder) holder;
            viewHolder.setPosition(position);
            viewHolder.setViewState(viewState);
            viewHolder.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        if (mGoalId == null) {
            return 0;
        }
        return mIdeaInteractor.getPendingIdeasCount(mGoalId);
    }
}
