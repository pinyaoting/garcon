package com.pinyaoting.garcondecuisine.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pinyaoting.garcondecuisine.R;

public class ConstantsAndUtils {
    public static final String USER_LISTS = "userLists";
    public static final String SHOPPING_LISTS = "shoppingLists";
    public static final String USERS = "users";
    public static final String ANONYMOUS = "anonymous";
    public static final String TIMESTAMP = "timestamp";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String IDEAS = "ideas";
    public static final String PLAN_ID = "planId";
    public static final String SHARED_WITH = "sharedWith";
    public static final String NOTIFY = "notify";
    public static final String POSITION = "position";
    public static final int LATEST = 25;
    static final String SPOONACULAR_IMAGE_CDN_BASE_URL =
            "https://spoonacular.com/cdn/ingredients_100x100/";
    public static final String SHARED_LIST_URL = "https://garcon-de-cuisine.firebaseapp.com/";


    public static String getOwner(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(EMAIL, ANONYMOUS);
    }

    public static String getDefaultTitle(Context context) {
        return context.getString(R.string.default_idea_prefix);
    }

    public static String getSpoonacularImageUrl(String path) {
        return new StringBuilder(SPOONACULAR_IMAGE_CDN_BASE_URL).append(path).toString();
    }

    public static int getAndroidSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
