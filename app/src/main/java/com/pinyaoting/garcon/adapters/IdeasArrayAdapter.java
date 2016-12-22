package com.pinyaoting.garcon.adapters;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.viewholders.IdeaViewHolder;
import com.pinyaoting.garcon.viewstates.Idea;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

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
        View view = inflater.inflate(R.layout.item_idea, parent, false);
        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Idea viewModel = mIdeaInteractor.getPendingIdea(mGoalId, position);
        if (holder instanceof IdeaViewHolder) {
            IdeaViewHolder viewHolder = (IdeaViewHolder) holder;
            viewHolder.setPosition(position);
            viewHolder.setViewModel(viewModel);
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
