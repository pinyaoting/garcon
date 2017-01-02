package com.pinyaoting.garcondecuisine.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.databinding.ToolbarMainBinding;

/**
 * Created by pinyaoting on 12/29/16.
 */

public class ToolbarUtils {

    public static void bind(AppCompatActivity activity, Toolbar toolbar) {
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public static void configureTitle(ToolbarMainBinding binding, String title, float titleSize) {
        binding.appBar.setExpanded(true, true);
        binding.toolbarTitle.setText(title);
        binding.toolbarTitle.setTextSize(titleSize);
    }

    public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(activity);
                return true;
        }
        return false;
    }
}
