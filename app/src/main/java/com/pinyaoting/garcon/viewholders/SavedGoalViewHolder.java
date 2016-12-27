package com.pinyaoting.garcon.viewholders;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.databinding.ItemSavedGoalBinding;
import com.pinyaoting.garcon.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcon.utils.ImageUtils;
import com.pinyaoting.garcon.viewstates.Goal;

/**
 * Created by pinyaoting on 12/27/16.
 */

public class SavedGoalViewHolder extends RecyclerView.ViewHolder {

    ItemSavedGoalBinding binding;

    public SavedGoalViewHolder(View itemView) {
        super(itemView);
        binding = ItemSavedGoalBinding.bind(itemView);
    }

    public void setPosition(int position) {
        int colorResource = ImageUtils.getColorForPosition(position);
        int color = ContextCompat.getColor(getContext(), colorResource);

        FrameLayout.LayoutParams paramsForImage = (FrameLayout.LayoutParams)
                binding.ivPlanImage.getLayoutParams();
        FrameLayout.LayoutParams paramsForBox = (FrameLayout.LayoutParams)
                binding.flDescriptionBox.getLayoutParams();
        if (position % 2 == 0) {
            paramsForImage.gravity = Gravity.LEFT | Gravity.START;
            paramsForBox.leftMargin = paramsForImage.width;
            paramsForBox.setMarginStart(paramsForImage.width);
            paramsForBox.rightMargin = 0;
            paramsForBox.setMarginEnd(0);
        } else {
            paramsForImage.gravity = Gravity.RIGHT | Gravity.END;
            paramsForBox.leftMargin = 0;
            paramsForBox.setMarginStart(0);
            paramsForBox.rightMargin = paramsForImage.width;
            paramsForBox.setMarginEnd(paramsForImage.width);
        }
        binding.llSinglePlan.setBackgroundColor(color);
        binding.setPos(position);
    }

    public void setViewState(Goal viewState) {
        ImageUtils.loadImageWithProminentColor(
                binding.ivPlanImage, binding.llSinglePlan, viewState.getImageUrl());
        binding.setViewState(viewState);
    }

    public void setHandler(GoalActionHandlerInterface actionHandler) {
        binding.setHandler(actionHandler);
    }

    public void executePendingBindings() {
        binding.executePendingBindings();
    }

    public void recycle() {
        binding.ivPlanImage.setImageResource(R.drawable.ic_placeholder);
    }
}
