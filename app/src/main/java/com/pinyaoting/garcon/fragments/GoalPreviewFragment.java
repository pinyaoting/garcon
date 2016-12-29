package com.pinyaoting.garcon.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.adapters.IdeasArrayAdapter;
import com.pinyaoting.garcon.databinding.FragmentGoalPreviewBinding;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalDetailActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.InjectorInterface;
import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.utils.ImageUtils;
import com.pinyaoting.garcon.viewstates.Goal;

import javax.inject.Inject;

import rx.Observer;

public class GoalPreviewFragment extends Fragment {

    static final String IDEA_PREVIEW_FRAGMENT_INDEX = "IDEA_PREVIEW_FRAGMENT_INDEX";

    FragmentGoalPreviewBinding binding;
    int mPos;
    @Inject
    GoalDetailActionHandlerInterface mActionHandler;
    @Inject
    GoalInteractorInterface mGoalInteractor;
    @Inject
    IdeaInteractorInterface mIdeaInteractor;
    IdeasArrayAdapter mIdeasArrayAdapter;

    public GoalPreviewFragment() {
        // Required empty public constructor
    }

    public static GoalPreviewFragment newInstance(int pos) {
        GoalPreviewFragment fragment = new GoalPreviewFragment();
        Bundle args = new Bundle();
        args.putInt(IDEA_PREVIEW_FRAGMENT_INDEX, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPos = getArguments().getInt(IDEA_PREVIEW_FRAGMENT_INDEX);
            Goal goal = mGoalInteractor.getGoalAtPos(mPos);
            mIdeasArrayAdapter = new IdeasArrayAdapter(mIdeaInteractor, goal.getId());
            mGoalInteractor.loadDetailsForGoalAtPos(mPos);
            mGoalInteractor.subscribeToGoalStateChange(new Observer<ViewState>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ViewState viewState) {
                    switch (viewState.getState()) {
                        case R.id.state_loaded:
                            switch (viewState.getOperation()) {
                                case UPDATE:
                                    if (viewState.getStart() != -1) {
                                        return;
                                    }
                                    Goal updatedGoal = mGoalInteractor.getGoalAtPos(mPos);
                                    mIdeasArrayAdapter.setGoalId(updatedGoal.getId());
                                    mIdeasArrayAdapter.notifyDataSetChanged();
                                    binding.setViewState(updatedGoal);
                                    binding.executePendingBindings();
                            }
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_goal_preview, container,
                false);
        binding.setHandler(mActionHandler);
        binding.rvGoalPreviewIdeas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvGoalPreviewIdeas.setAdapter(mIdeasArrayAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.line_divider_edge_to_edge));
        binding.rvGoalPreviewIdeas.addItemDecoration(dividerItemDecoration);
        binding.nsvGoalPreviewIdeasContainer.setNestedScrollingEnabled(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Goal viewState = mGoalInteractor.getGoalAtPos(mPos);
        binding.setPos(mPos);
        binding.setViewState(viewState);

        if (viewState.getSubTitle() == null) {
            binding.ivGoalPreviewSubTitle.setAlpha(0.0f);
            binding.tvGoalPreviewSubTitle.setAlpha(0.0f);
        } else {
            binding.ivGoalPreviewSubTitle.setAlpha(0.5f);
            binding.tvGoalPreviewSubTitle.setAlpha(1.0f);
        }

        SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap,
                                        GlideAnimation<? super Bitmap> glideAnimation) {
                // insert the bitmap into the image view
                binding.ivIdeaBackgroundImage.setImageBitmap(bitmap);

                // Use generate() method from the Palette API to get the vibrant color from the
                // bitmap
                // Set the result as the background color
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            int color = vibrant.getRgb();
                            int colorWithAlpha = ImageUtils.getTransparentColor(color);
                            binding.tvGoalPreviewIndex.setBackgroundTintList(
                                    ColorStateList.valueOf(color));
                            binding.tvGoalPreviewTitle.setTextColor(color);
                            binding.tvGoalPreviewSubTitle.setTextColor(colorWithAlpha);
                            binding.ivGoalPreviewSubTitle.setImageTintList(
                                    ColorStateList.valueOf(color));
                        }
                    }
                });
            }
        };

        if (viewState.getImageUrl() != null) {
            Glide.with(binding.ivIdeaBackgroundImage.getContext())
                    .load(viewState.getImageUrl())
                    .asBitmap()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(target);
        }

        binding.executePendingBindings();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InjectorInterface) {
            InjectorInterface injector = (InjectorInterface) context;
            injector.inject(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
