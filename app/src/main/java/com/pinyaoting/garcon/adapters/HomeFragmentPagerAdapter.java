package com.pinyaoting.garcon.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pinyaoting.garcon.fragments.GoalSearchFragment;
import com.pinyaoting.garcon.fragments.IdeaListFragment;
import com.pinyaoting.garcon.fragments.SavedGoalsFragment;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int SEARCH_GOAL = 0;
    public static final int MY_IDEAS = 1;
    public static final int SAVED_GOALS = 2;


    private String tabTitles[] =
            new String[]{"Explore Goals", "My Ideas", "Saved Goals"};
    private Context mContext;
    private GoalSearchFragment mGoalSearchFragment;
    private SavedGoalsFragment mSavedGoalFragment;
    private IdeaListFragment mMyIdeasFragment;

    public HomeFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SEARCH_GOAL:
                return getGoalSearchFragment();
            case MY_IDEAS:
                return getMyIdeasFragment();
            case SAVED_GOALS:
                return getSavedGoalFragment();
            default:
                return null;
        }
    }

    public GoalSearchFragment getGoalSearchFragment() {
        if (mGoalSearchFragment == null) {
            mGoalSearchFragment = GoalSearchFragment.newInstance();
        }
        return mGoalSearchFragment;
    }

    public SavedGoalsFragment getSavedGoalFragment() {
        if (mSavedGoalFragment == null) {
            mSavedGoalFragment = SavedGoalsFragment.newInstance();
        }
        return mSavedGoalFragment;
    }

    public IdeaListFragment getMyIdeasFragment() {
        if (mMyIdeasFragment == null) {
            mMyIdeasFragment = IdeaListFragment.newInstance();
        }
        return mMyIdeasFragment;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
