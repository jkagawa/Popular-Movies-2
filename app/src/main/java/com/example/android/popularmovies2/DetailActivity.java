package com.example.android.popularmovies2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.utilities.NetworkUtils;
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
    private RecyclerView mRecyclerView;

    private static final int NUMBER_OF_ITEMS = 100;

    public static final String VIDEO_ID_KEY = "id";
    public static final String VIDEO_ISO_639_KEY = "iso_639_1";
    public static final String VIDEO_ISO_3166_KEY = "iso_3166_1";
    public static final String VIDEO_KEY_KEY = "key";
    public static final String VIDEO_NAME_KEY = "name";
    public static final String VIDEO_SITE_KEY = "site";
    public static final String VIDEO_SIZE_KEY = "size";
    public static final String VIDEO_TYPE_KEY = "type";

    private List<String> mVideoId = new ArrayList<>();
    private List<String> mVideo_iso_639_1_List = new ArrayList<>();
    private List<String> mVideo_iso_3166_1_List = new ArrayList<>();
    private List<String> mVideoKeyList = new ArrayList<>();
    private List<String> mVideoNameList = new ArrayList<>();
    private List<String> mVideoSiteList = new ArrayList<>();
    private List<String> mVideoSizeList = new ArrayList<>();
    private List<String> mVideoTypeList = new ArrayList<>();

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

        Intent intentFromMainActivity = getIntent();

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            String movieID = intentFromMainActivity.getStringExtra("EXTRA_MOVIE_ID");
            String ratingCount = intentFromMainActivity.getStringExtra("EXTRA_RATING_COUNT");
            String rating = intentFromMainActivity.getStringExtra("EXTRA_RATING");
            String title = intentFromMainActivity.getStringExtra("EXTRA_TITLE");
            String posterPath = intentFromMainActivity.getStringExtra("EXTRA_POSTER");
            String backdrop = intentFromMainActivity.getStringExtra("EXTRA_BACKDROP");
            String overview = intentFromMainActivity.getStringExtra("EXTRA_OVERVIEW");
            String releaseDate = intentFromMainActivity.getStringExtra("EXTRA_RELEASE_DATE");

            URL posterURL = NetworkUtils.buildImageUrl(posterPath,"w500");
            URL backdropURL = NetworkUtils.buildImageUrl(backdrop,"original");

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
            mVoteAverage.setText(rating + " / 10");

            //Set Vote Count
            int ratingCountInt = Integer.parseInt(ratingCount);
            ratingCount = NumberFormat.getIntegerInstance(Locale.US).format(ratingCountInt);
            mVoteCounts.setText(ratingCount + " votes");

            //Set Plot Summary
            mOverview.setText(overview);

            //Format Date
            SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = null;
            try {
                date = oldDateFormat.parse(releaseDate);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            SimpleDateFormat dateFormater = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            String outputDate = dateFormater.format(date);

            //Set Release Date
            mReleaseDate.setText(outputDate);

            //Set header text to the movie title
            setTitle(title);

            mRecyclerView = findViewById(R.id.recyclerview_videos);

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

            GridLayoutManager layoutManager = new GridLayoutManager(this, gridSpan);
            mRecyclerView.setLayoutManager(layoutManager);

            mVideoAdapter = new VideoAdapter(NUMBER_OF_ITEMS,this);

            mRecyclerView.setAdapter(mVideoAdapter);

            makeUrlQuery(movieID);
        }

    }

    private void makeUrlQuery(String movieID) {
        URL videoJSONUrl = NetworkUtils.buildVideoJSONUrl(API_KEY, movieID);
        //URL reviewJSONUrl = NetworkUtils.buildReviewJSONUrl(API_KEY, movieID);
        new DetailActivity.UrlQueryTask().execute(videoJSONUrl);
        //new DetailActivity.UrlQueryTask().execute(reviewJSONUrl);
    }

    @Override
    public void onClick(int position) {

    }

    public class UrlQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];

            String urlResults = null;
            try {
                urlResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return urlResults;
        }

        @Override
        protected void onPostExecute(String s) {
            String jsonFromUrl;

            if (s != null && !s.equals("")) {
                jsonFromUrl = s;
                try {
                    JSONObject json = new JSONObject(jsonFromUrl);
                    JSONArray results = json.getJSONArray("results");

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
        }
    }

}
