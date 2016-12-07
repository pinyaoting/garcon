package com.doublesp.coherence.adapters;

import com.doublesp.coherence.R;
import com.doublesp.coherence.interfaces.domain.IdeaInteractorInterface;
import com.doublesp.coherence.viewholders.SimpleIdeaViewHolder;
import com.doublesp.coherence.viewmodels.Idea;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by pinyaoting on 12/6/16.
 */

public class IdeasArrayAdapter extends RecyclerView.Adapter {

    @Inject
    IdeaInteractorInterface mIdeaInteractor;

    public IdeasArrayAdapter(IdeaInteractorInterface ideaInteractor) {
        mIdeaInteractor = ideaInteractor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.simple_item_idea, parent, false);
        return new SimpleIdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Idea viewModel = mIdeaInteractor.getPendingIdeaAtPos(position);
        if (holder instanceof SimpleIdeaViewHolder) {
            SimpleIdeaViewHolder viewHolder = (SimpleIdeaViewHolder) holder;
            viewHolder.setPosition(position);
            viewHolder.setViewModel(viewModel);
            viewHolder.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return mIdeaInteractor.getPendingIdeasCount();
    }
}
