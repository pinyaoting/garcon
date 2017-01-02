package com.pinyaoting.garcondecuisine.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.adapters.GoalPreviewFragmentPagerAdapter;
import com.pinyaoting.garcondecuisine.databinding.FragmentGoalDetailViewPagerBinding;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.InjectorInterface;

import javax.inject.Inject;

public class GoalDetailViewPagerFragment extends Fragment {

    static final String GOAL_DETAIL_VIEW_PAGER_INDEX = "GOAL_DETAIL_VIEW_PAGER_INDEX";
    FragmentGoalDetailViewPagerBinding binding;
    GoalPreviewFragmentPagerAdapter mPagerAdapter;
    int currentIndex;

    @Inject
    GoalInteractorInterface mGoalInteractor;

    public GoalDetailViewPagerFragment() {
        // Required empty public constructor
    }

    public static GoalDetailViewPagerFragment newInstance(int pos) {
        GoalDetailViewPagerFragment fragment = new GoalDetailViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt(GOAL_DETAIL_VIEW_PAGER_INDEX, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         if (getArguments() != null) {
            currentIndex = getArguments().getInt(GOAL_DETAIL_VIEW_PAGER_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(GOAL_DETAIL_VIEW_PAGER_INDEX, currentIndex);
        }
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_goal_detail_view_pager, container, false);
        if (getActivity() instanceof InjectorInterface) {
            InjectorInterface injector = (InjectorInterface) getActivity();
            injector.inject(this);
        }
        mPagerAdapter = new GoalPreviewFragmentPagerAdapter(
                getChildFragmentManager(), mGoalInteractor);
        binding.viewpager.setAdapter(mPagerAdapter);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                notifyGainFocus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.viewpager.setCurrentItem(currentIndex, true);
        mGoalInteractor.bookmarkGoalAtPos(currentIndex);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void notifyGainFocus(int pos) {
        Fragment fragment = mPagerAdapter.getItem(pos);
        if (fragment instanceof GoalPreviewFragment) {
            GoalPreviewFragment goalPreviewFragment = (GoalPreviewFragment) fragment;
            goalPreviewFragment.didGainFocus();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(GOAL_DETAIL_VIEW_PAGER_INDEX, currentIndex);
    }
}
