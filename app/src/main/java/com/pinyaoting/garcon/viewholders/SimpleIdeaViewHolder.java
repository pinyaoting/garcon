package com.pinyaoting.garcon.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pinyaoting.garcon.databinding.ItemIdeaBinding;
import com.pinyaoting.garcon.utils.BindingAdapterUtils;
import com.pinyaoting.garcon.viewstates.Idea;

/**
 * Created by pinyaoting on 12/6/16.
 */

public class SimpleIdeaViewHolder extends RecyclerView.ViewHolder {

    ItemIdeaBinding binding;

    public SimpleIdeaViewHolder(View itemView) {
        super(itemView);
        binding = ItemIdeaBinding.bind(itemView);
    }

    public void setPosition(int position) {
        binding.setPos(position);
    }

    public void setViewState(Idea viewState) {
        binding.setViewState(viewState);
        if (viewState == null ||
                viewState.getMeta() == null ||
                viewState.getMeta().getImageUrl() == null) {
            return;
        }
        BindingAdapterUtils.loadImage(binding.ivIdea, viewState.getMeta().getImageUrl());
    }

    public void executePendingBindings() {
        binding.executePendingBindings();
    }
}
