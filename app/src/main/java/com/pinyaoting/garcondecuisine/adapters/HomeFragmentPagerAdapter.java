package com.pinyaoting.garcondecuisine.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pinyaoting.garcondecuisine.fragments.GoalSearchFragment;
import com.pinyaoting.garcondecuisine.fragments.IdeaListFragment;
import com.pinyaoting.garcondecuisine.fragments.SavedGoalsFragment;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int SEARCH_GOAL = 0;
    public static final int MY_IDEAS = 1;
    public static final int SAVED_GOALS = 2;


    private String tabTitles[] =
            new String[]{"Explore Goals", "My Ideas", "Saved Goals"};
    private Context mContext;

    public HomeFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SEARCH_GOAL:
                return GoalSearchFragment.newInstance();
            case MY_IDEAS:
                return IdeaListFragment.newInstance();
            case SAVED_GOALS:
                return SavedGoalsFragment.newInstance();
            default:
                return null;
        }
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
