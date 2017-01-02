package com.pinyaoting.garcondecuisine.viewholders;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.databinding.ItemGoalBinding;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcondecuisine.utils.ImageUtils;
import com.pinyaoting.garcondecuisine.viewstates.Goal;

public class GoalViewHolder extends RecyclerView.ViewHolder {

    public ItemGoalBinding binding;

    public GoalViewHolder(View itemView) {
        super(itemView);
        binding = ItemGoalBinding.bind(itemView);
        binding.setViewHolder(this); // NOTE: circular reference
    }

    public void setPosition(int position) {
        binding.setPos(position);
    }

    public void setViewState(Goal viewState) {
        binding.setViewState(viewState);

        if (viewState.getSubTitle() == null) {
            binding.ivGoalSubTitle.setAlpha(0.0f);
            binding.btnGoalSubTitle.setAlpha(0.0f);
        } else {
            binding.ivGoalSubTitle.setAlpha(0.5f);
            binding.btnGoalSubTitle.setAlpha(1.0f);
        }

        // TODO: do palette with data bind
        SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap,
                                        GlideAnimation<? super Bitmap> glideAnimation) {
                // insert the bitmap into the image view
                binding.ivGoalImage.setImageBitmap(bitmap);

                // Use generate() method from the Palette API to get the vibrant color from the
                // bitmap
                // Set the result as the background color
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            int colorFrom = binding.tvGoalIndex.getTextColors().getDefaultColor();
                            int colorTo = vibrant.getRgb();
                            int colorWithAlpha = ImageUtils.getTransparentColor(colorTo);
                            if (colorFrom == colorTo) {
                                return;
                            }

                            // Update the title TextView with the proper text color
                            binding.tvGoalTitle.setTextColor(colorTo);
                            binding.tvGoalIndex.setTextColor(colorTo);
                            binding.btnGoalSubTitle.setTextColor(colorWithAlpha);

                            // Set the background color of a layout based on the vibrant color
                            binding.tvGoalTitle.setBackgroundTintList(
                                    ColorStateList.valueOf(colorTo));
                            binding.tvGoalIndex.setBackgroundTintList(
                                    ColorStateList.valueOf(colorTo));
                            binding.ivGoalSubTitle.setImageTintList(
                                    ColorStateList.valueOf(colorTo));
                        }
                    }
                });
            }
        };

        Glide.with(binding.ivGoalImage.getContext())
                .load(viewState.getImageUrl())
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(target);
    }

    public void setHandler(GoalActionHandlerInterface handler) {
        binding.setHandler(handler);
    }

    public void executePendingBindings() {
        binding.executePendingBindings();
    }

    public void recycle() {
        binding.ivGoalImage.setImageResource(R.drawable.ic_placeholder);
    }
}
