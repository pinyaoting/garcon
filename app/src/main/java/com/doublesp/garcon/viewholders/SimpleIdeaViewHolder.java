package com.doublesp.garcon.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.doublesp.garcon.databinding.ItemIdeaBinding;
import com.doublesp.garcon.utils.BindingAdapterUtils;
import com.doublesp.garcon.viewmodels.Idea;

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

    public void setViewModel(Idea viewModel) {
        binding.setViewModel(viewModel);
        if (viewModel == null ||
                viewModel.getMeta() == null ||
                viewModel.getMeta().getImageUrl() == null) {
            return;
        }
        BindingAdapterUtils.loadImage(binding.ivIdea, viewModel.getMeta().getImageUrl());
    }

    public void executePendingBindings() {
        binding.executePendingBindings();
    }
}
