package com.pinyaoting.garcon.viewholders;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.databinding.ItemIdeaBinding;
import com.pinyaoting.garcon.utils.BindingAdapterUtils;
import com.pinyaoting.garcon.utils.ImageUtils;
import com.pinyaoting.garcon.viewstates.Idea;

public class IdeaViewHolder extends RecyclerView.ViewHolder {

    ItemIdeaBinding binding;

    public IdeaViewHolder(View itemView) {
        super(itemView);
        binding = ItemIdeaBinding.bind(itemView);
    }

    public void setPosition(int position) {
        binding.setPos(position);
        int color = ImageUtils.getColorForPosition(position);
        binding.flSimpleItemIdea.setBackgroundColor(color);
    }

    public void setViewState(final Idea viewState) {
        binding.setViewState(viewState);

        if (viewState == null ||
                viewState.getMeta() == null ||
                viewState.getMeta().getImageUrl() == null) {
            return;
        }

        if (viewState.isCrossedOut()) {
            // grey-out the line
            binding.flSimpleItemIdea.setBackgroundColor(
                    ContextCompat.getColor(binding.ivIdea.getContext(), R.color.colorWhiteOverlay));
            BindingAdapterUtils.loadImage(binding.ivIdea, viewState.getMeta().getImageUrl());
            return;
        }

        // TODO: do palette with data bind
        SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap,
                                        GlideAnimation<? super Bitmap> glideAnimation) {
                // insert the bitmap into the image view
                binding.ivIdea.setImageBitmap(bitmap);

                // Use generate() method from the Palette API to get the vibrant color from the
                // bitmap
                // Set the result as the background color
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            ColorDrawable bgDrawable = (ColorDrawable) binding
                                    .flSimpleItemIdea.getBackground();
                            int colorFrom = bgDrawable.getColor();
                            int colorTo = vibrant.getRgb();
                            int colorToWithAlpha = ImageUtils.getIlluminatedColor(colorTo);

                            ValueAnimator colorAnimation = ValueAnimator.ofObject(
                                    new ArgbEvaluator(), colorFrom, colorToWithAlpha);
                            colorAnimation.setDuration(250); // milliseconds
                            colorAnimation.addUpdateListener(
                                    new ValueAnimator.AnimatorUpdateListener() {

                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            binding.flSimpleItemIdea
                                                    .setBackgroundColor(
                                                            (int) animator.getAnimatedValue());
                                        }

                                    });
                            colorAnimation.start();
                        }
                    }
                });
            }
        };

        Glide.with(binding.ivIdea.getContext())
                .load(viewState.getMeta().getImageUrl())
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(target);
    }

    public void executePendingBindings() {
        binding.executePendingBindings();
    }
}
