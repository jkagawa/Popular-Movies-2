package com.example.android.popularmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Joshua on 6/10/2018.
 */

public class FavoriteContract {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.android.popularmovies2";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY );
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_USER_RATING = "user_rating";

        public static final String COLUMN_USER_RATING_COUNT = "user_rating_count";

        public static final String COLUMN_BACKDROP = "backdrop";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
