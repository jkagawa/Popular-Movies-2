package com.example.android.popularmovies2;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.data.FavoriteContract;
import com.example.android.popularmovies2.data.FavoriteDbHelper;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.android.popularmovies2.MainActivity.API_KEY;

/**
 * Created by Joshua on 5/24/2018.
 */

public class DetailActivity extends AppCompatActivity implements VideoAdapter.VideoAdapterOnClickHandler {

    private ImageView mPoster;
    private ImageView mBackdrop;

    private TextView mVoteCounts;
    private TextView mVoteAverage;
    private TextView mOverview;
    private TextView mReleaseDate;

    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView mVideoRecyclerView;
    private RecyclerView mReviewRecyclerView;

    private String movieID;
    private String ratingCount;
    private String rating;
    private String title;
    private String posterPath;
    private String backdrop;
    private String overview;
    private String releaseDate;

    private static final int NUMBER_OF_ITEMS = 100;

    public static final String VIDEO_ID_KEY = "id";
    public static final String VIDEO_ISO_639_KEY = "iso_639_1";
    public static final String VIDEO_ISO_3166_KEY = "iso_3166_1";
    public static final String VIDEO_KEY_KEY = "key";
    public static final String VIDEO_NAME_KEY = "name";
    public static final String VIDEO_SITE_KEY = "site";
    public static final String VIDEO_SIZE_KEY = "size";
    public static final String VIDEO_TYPE_KEY = "type";

    public static final String REVIEW_AUTHOR_KEY = "author";
    public static final String REVIEW_CONTENT_KEY = "content";
    public static final String REVIEW_ID_KEY = "id";
    public static final String REVIEW_URL_KEY = "url";

    public static final String IMAGE_WIDTH_W500 = "w500";
    public static final String IMAGE_WIDTH_ORIGINAL = "original";

    public static final String DATE_PATTERN_1 = "yyyy-MM-dd";
    public static final String DATE_PATTERN_2 = "MMMM dd, yyyy";


    private List<String> mVideoId = new ArrayList<>();
    private List<String> mVideo_iso_639_1_List = new ArrayList<>();
    private List<String> mVideo_iso_3166_1_List = new ArrayList<>();
    private List<String> mVideoKeyList = new ArrayList<>();
    private List<String> mVideoNameList = new ArrayList<>();
    private List<String> mVideoSiteList = new ArrayList<>();
    private List<String> mVideoSizeList = new ArrayList<>();
    private List<String> mVideoTypeList = new ArrayList<>();

    private List<String> mReviewAuthor = new ArrayList<>();
    private List<String> mReviewContent = new ArrayList<>();
    private List<String> mReviewId = new ArrayList<>();
    private List<String> mReviewUrl = new ArrayList<>();

    private SQLiteDatabase mDatabase;

    private Button mFavoriteButton;
    private Button mRemoveFavoriteButton;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPoster = findViewById(R.id.detail_poster);
        mBackdrop = findViewById(R.id.detail_backdrop);

        mVoteCounts = findViewById(R.id.detail_vote_count);
        mVoteAverage = findViewById(R.id.detail_rating);
        mOverview = findViewById(R.id.detail_overview);
        mReleaseDate = findViewById(R.id.detail_release_date);

        mFavoriteButton = findViewById(R.id.detail_favorite_button);
        mRemoveFavoriteButton = findViewById(R.id.detail_remove_favorite_button);

        Intent intentFromMainActivity = getIntent();

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            movieID = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_MOVIE_ID_KEY);
            ratingCount = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_RATING_COUNT_KEY);
            rating = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_RATING_KEY);
            title = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_TITLE_KEY);
            posterPath = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_POSTER_KEY);
            backdrop = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_BACKDROP_KEY);
            overview = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_OVERVIEW_KEY);
            releaseDate = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_RELEASE_DATE_KEY);

            FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
            mDatabase = dbHelper.getWritableDatabase();

            if(checkIfInFavorites(movieID)) {
                mFavoriteButton.setVisibility(View.INVISIBLE);
                mRemoveFavoriteButton.setVisibility(View.VISIBLE);
            }
            else {
                mRemoveFavoriteButton.setVisibility(View.INVISIBLE);
                mFavoriteButton.setVisibility(View.VISIBLE);
            }


            URL posterURL = NetworkUtils.buildImageUrl(posterPath,IMAGE_WIDTH_W500);
            URL backdropURL = NetworkUtils.buildImageUrl(backdrop,IMAGE_WIDTH_ORIGINAL);

            //Set poster image
            Picasso.with(this)
                    .load(posterURL.toString())
                    .error(R.drawable.no_image_poster)
                    .into(mPoster);

            //Set backdrop image
            Picasso.with(this)
                    .load(backdropURL.toString())
                    .placeholder(R.drawable.placeholder_backdrop)
                    .error(R.drawable.no_image_backdrop)
                    .into(mBackdrop);

            //Set Rating
            mVoteAverage.setText(getResources().getString(R.string.rating, rating));

            //Set Vote Count
            int ratingCountInt = Integer.parseInt(ratingCount);
            ratingCount = NumberFormat.getIntegerInstance(Locale.US).format(ratingCountInt);
            mVoteCounts.setText(getResources().getString(R.string.vote_count, ratingCount));

            //Set Plot Summary
            mOverview.setText(overview);

            //Format Date
            SimpleDateFormat oldDateFormat = new SimpleDateFormat(DATE_PATTERN_1, Locale.US);
            Date date = null;
            try {
                date = oldDateFormat.parse(releaseDate);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_PATTERN_2, Locale.US);
            String outputDate = dateFormater.format(date);

            //Set Release Date
            mReleaseDate.setText(outputDate);

            //Set header text to the movie title
            setTitle(title);

            mVideoRecyclerView = findViewById(R.id.recyclerview_videos);
            mReviewRecyclerView = findViewById(R.id.recyclerview_reviews);

            /*
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            int gridSpan = 2;
            if(width > 2000) {
                gridSpan = 4;
            }
            else if(width > 1500) {
                gridSpan = 3;
            }
            */

            //GridLayoutManager layoutManager = new GridLayoutManager(this, gridSpan);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mVideoRecyclerView.setLayoutManager(layoutManager);
            mReviewRecyclerView.setLayoutManager(layoutManager2);

            mVideoAdapter = new VideoAdapter(NUMBER_OF_ITEMS,this);
            mReviewAdapter = new ReviewAdapter(NUMBER_OF_ITEMS);

            mVideoRecyclerView.setAdapter(mVideoAdapter);
            mReviewRecyclerView.setAdapter(mReviewAdapter);

            makeUrlQuery(movieID);

        }

    }



    private void makeUrlQuery(String movieID) {
        //URL videoJSONUrl = NetworkUtils.buildVideoJSONUrl(API_KEY, movieID);
        //URL reviewJSONUrl = NetworkUtils.buildReviewJSONUrl(API_KEY, movieID);
        new DetailActivity.UrlQueryTask().execute(movieID);
        //new DetailActivity.UrlQueryTask().execute(reviewJSONUrl);
    }

    @Override
    public void onClick(int position) {

    }

    public class UrlQueryTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            String movieID = params[0];
            //URL searchUrl = params[0];

            URL videoJSONUrl = NetworkUtils.buildVideoJSONUrl(API_KEY, movieID);
            URL reviewJSONUrl = NetworkUtils.buildReviewJSONUrl(API_KEY, movieID);

            List<String> urlResultsList  = new ArrayList<>();

            String urlResults = null;
            try {
                urlResultsList.add(NetworkUtils.getResponseFromHttpUrl(videoJSONUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlResultsList.add(NetworkUtils.getResponseFromHttpUrl(reviewJSONUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return urlResultsList;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            String jsonFromUrl;

            //Video URL results
            if (list.get(0) != null && !list.get(0).equals("")) {
                jsonFromUrl = list.get(0);
                try {
                    JSONObject json = new JSONObject(jsonFromUrl);
                    JSONArray results = json.getJSONArray(MainActivity.RESULTS_KEY);

                    List<String> videoIdList = new ArrayList<>();
                    List<String> video_iso_639_1_List = new ArrayList<>();
                    List<String> video_iso_3166_1_List = new ArrayList<>();
                    List<String> videoKeyList = new ArrayList<>();
                    List<String> videoNameList = new ArrayList<>();
                    List<String> videoSiteList = new ArrayList<>();
                    List<String> videoSizeList = new ArrayList<>();
                    List<String> videoTypeList = new ArrayList<>();

                    int resultsLength = results.length();

                    for(int i = 0; i<resultsLength; i++) {
                        JSONObject focus = results.getJSONObject(i);

                        videoIdList.add(focus.optString(VIDEO_ID_KEY));
                        video_iso_639_1_List.add(focus.optString(VIDEO_ISO_639_KEY));
                        video_iso_3166_1_List.add(focus.optString(VIDEO_ISO_3166_KEY));
                        videoKeyList.add(focus.optString(VIDEO_KEY_KEY));
                        videoNameList.add(focus.optString(VIDEO_NAME_KEY));
                        videoSiteList.add(focus.optString(VIDEO_SITE_KEY));
                        videoSizeList.add(focus.optString(VIDEO_SIZE_KEY));
                        videoTypeList.add(focus.optString(VIDEO_TYPE_KEY));

                    }

                    mVideoId = videoIdList;
                    mVideo_iso_639_1_List = video_iso_639_1_List;
                    mVideo_iso_3166_1_List = video_iso_3166_1_List;
                    mVideoKeyList = videoKeyList;
                    mVideoNameList = videoNameList;
                    mVideoSiteList = videoSiteList;
                    mVideoSizeList = videoSizeList;
                    mVideoTypeList = videoTypeList;
                    mVideoAdapter.setData(mVideoKeyList, mVideoNameList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //Review URL results
            if (list.get(1) != null && !list.get(1).equals("")) {
                jsonFromUrl = list.get(1);
                try {
                    JSONObject json = new JSONObject(jsonFromUrl);
                    JSONArray results = json.getJSONArray(MainActivity.RESULTS_KEY);

                    List<String> reviewAuthorList = new ArrayList<>();
                    List<String> reviewContentList = new ArrayList<>();
                    List<String> reviewIdList = new ArrayList<>();
                    List<String> reviewUrlList = new ArrayList<>();

                    int resultsLength = results.length();

                    for(int i = 0; i<resultsLength; i++) {
                        JSONObject focus = results.getJSONObject(i);

                        reviewAuthorList.add(focus.optString(REVIEW_AUTHOR_KEY));
                        reviewContentList.add(focus.optString(REVIEW_CONTENT_KEY));
                        reviewIdList.add(focus.optString(REVIEW_ID_KEY));
                        reviewUrlList.add(focus.optString(REVIEW_URL_KEY));

                    }

                    mReviewAuthor = reviewAuthorList;
                    mReviewContent = reviewContentList;
                    mReviewId = reviewIdList;
                    mReviewUrl = reviewUrlList;

                    mReviewAdapter.setData(mReviewAuthor, mReviewContent);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void addToFavoritesButton(View view) {

        addToFavorites(title, movieID, posterPath, overview, rating, ratingCount, backdrop, releaseDate);

        mFavoriteButton.setVisibility(View.INVISIBLE);
        mRemoveFavoriteButton.setVisibility(View.VISIBLE);

        /*
        if(mToast!=null) {
            mToast.cancel();
        }
        String message = "Movie title: "+(title);
        mToast.makeText(this, message, Toast.LENGTH_SHORT).show();
        */
    }

    public void removeFromFavoritesButton(View view) {

        removeFromFavorites(movieID);
        mRemoveFavoriteButton.setVisibility(View.INVISIBLE);
        mFavoriteButton.setVisibility(View.VISIBLE);

    }

    private void addToFavorites(String title, String movie_id, String poster, String synopsis, String user_rating, String user_rating_count, String backdrop, String release_date) {

        ContentValues contentValues = new ContentValues();

        //String query = "Select * from " + FavoriteContract.FavoriteEntry.TABLE_NAME + " where " + FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + movieID;
        //Cursor cursor = mDatabase.rawQuery(query, null);

        Cursor cursor = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + movieID,
                null,
                null);

        if(cursor.getCount() <=0) {

            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, title);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, movie_id);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER, poster);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, synopsis);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING, user_rating);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING_COUNT, user_rating_count);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP, backdrop);
            contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, release_date);
        }

        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);

        /*
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        */

        //return mDatabase.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, contentValues);

    }

    private boolean removeFromFavorites(String movieID) {

        return getContentResolver().delete(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=" + movieID,
                null) > 0;

        /*
        return mDatabase.delete(
                FavoriteContract.FavoriteEntry.TABLE_NAME,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=" + movieID, null) > 0;
        */

    }

    private boolean checkIfInFavorites(String movieID) {

        //String query = "Select * from " + FavoriteContract.FavoriteEntry.TABLE_NAME + " where " + FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + movieID;
        //Cursor cursor = mDatabase.rawQuery(query, null);

        Cursor cursor = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + movieID,
                null,
                null);

        if(cursor.getCount() <=0) {
            return false;
        }
        return true;

    }

}
