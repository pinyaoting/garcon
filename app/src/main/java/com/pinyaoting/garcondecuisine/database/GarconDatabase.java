package com.pinyaoting.garcondecuisine.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;

/**
 * Created by pinyaoting on 12/31/16.
 */

@ContentProvider(authority = GarconDatabase.AUTHORITY,
        database = GarconDatabase.class,
        baseContentUri = GarconDatabase.BASE_CONTENT_URI)
@Database(name = GarconDatabase.NAME, version = GarconDatabase.VERSION)
public class GarconDatabase {
    public static final String NAME = "GarconDatabase";
    public static final int VERSION = 3;
    public static final String AUTHORITY = "com.pinyaoting.garcondecuisine.provider";
    public static final String BASE_CONTENT_URI = "content://";
}
