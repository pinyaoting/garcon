package com.doublesp.coherence.fragments;

import com.doublesp.coherence.R;
import com.doublesp.coherence.databinding.FragmentIdeaSearchResultBinding;
import com.doublesp.coherence.interfaces.presentation.InjectorInterface;
import com.doublesp.coherence.utils.AnimationUtils;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import javax.inject.Inject;
import javax.inject.Named;

import static com.doublesp.coherence.fragments.ListCompositionFragment.LIST_COMPOSITION_BACKGROUND_IMAGE_ROTATION_INTERVAL;

public class IdeaSearchResultFragment extends DialogFragment {

    static final int IDEA_SEARCH_RESULT_SPANS = 2;
    FragmentIdeaSearchResultBinding binding;
    int[] mBackgroundImageIds;
    int mBackgroundImageIndex;

    @Inject
    @Named("Search")
    RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

    public IdeaSearchResultFragment() {
        // Required empty public constructor
    }

    public static IdeaSearchResultFragment newInstance() {
        IdeaSearchResultFragment fragment = new IdeaSearchResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setupBackgroundImageId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_idea_search_result, container, false);
        binding.rvIdeaSearchResults.setLayoutManager(new StaggeredGridLayoutManager(IDEA_SEARCH_RESULT_SPANS, StaggeredGridLayoutManager.VERTICAL));
        binding.rvIdeaSearchResults.setAdapter(mAdapter);
        rotateImage();
        return binding.getRoot();
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
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void setupBackgroundImageId() {
        mBackgroundImageIds = new int[5];
        mBackgroundImageIds[0] = R.drawable.background_0;
        mBackgroundImageIds[1] = R.drawable.background_1;
        mBackgroundImageIds[2] = R.drawable.background_2;
        mBackgroundImageIds[3] = R.drawable.background_3;
        mBackgroundImageIds[4] = R.drawable.background_4;
        mBackgroundImageIndex = 0;
    }

    void rotateImage() {
        binding.ivIdeaSearchBackground.setImageResource(
                mBackgroundImageIds[mBackgroundImageIndex]);
        binding.ivIdeaSearchBackground.startAnimation(AnimationUtils.fadeInOutAnimation(
                LIST_COMPOSITION_BACKGROUND_IMAGE_ROTATION_INTERVAL));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackgroundImageIndex = (mBackgroundImageIndex + 1) % mBackgroundImageIds.length;
                rotateImage();
            }
        }, LIST_COMPOSITION_BACKGROUND_IMAGE_ROTATION_INTERVAL);
    }
}
