package com.example.android.popularmovies2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies2.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private List<String> mMovieID = new ArrayList<>();
    private List<String> mVoteCounts = new ArrayList<>();
    private List<String> mVoteAverage = new ArrayList<>();
    private List<String> mTitle = new ArrayList<>();
    private List<String> mPoster = new ArrayList<>();
    private List<String> mBackdropPath = new ArrayList<>();
    private List<String> mOverview = new ArrayList<>();
    private List<String> mReleaseDate = new ArrayList<>();

    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;

    public static final String VOTE_COUNT_KEY = "vote_count";
    public static final String ID_KEY = "id";
    public static final String VIDEO_KEY = "video";
    public static final String VOTE_AVERAGE_KEY = "vote_average";
    public static final String TITLE_KEY = "title";
    public static final String POPULARITY_KEY = "popularity";
    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String ORIGINAL_LANGUAGE_KEY = "original_language";
    public static final String ORIGINAL_TITLE_KEY = "original_title";
    public static final String GENRE_IDS_KEY = "genre_ids";
    public static final String BACKDROP_PATH_KEY = "backdrop_path";
    public static final String ADULT_KEY = "adult";
    public static final String OVERVIEW_KEY = "overview";
    public static final String RELEASE_DATE_KEY = "release_date";

    final static int SORT_POPULARITY_KEY = 1;
    final static int SORT_RATING_KEY = 2;
    final static int SORT_FAVORITE_KEY = 3;

    private static final int NUMBER_OF_ITEMS = 100;

    public final static String API_KEY = "INSERT-API-KEY-HERE";

    //private Toast mToast;

    final static int SORT_POPULARITY_INDEX = 0;
    final static int SORT_RATING_INDEX = 1;
    final static int SORT_FAVORITE_INDEX = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_movies);


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

        mMovieAdapter = new MovieAdapter(NUMBER_OF_ITEMS,this);

        mRecyclerView.setAdapter(mMovieAdapter);

        makeUrlQuery(SORT_POPULARITY_KEY);


    }

    private void makeUrlQuery(int sortKey) {
        URL popularUrl = NetworkUtils.buildPopularUrl(API_KEY);
        URL topRatedUrl = NetworkUtils.buildTopRatedUrl(API_KEY);
        if(sortKey == 1) {
            new UrlQueryTask().execute(popularUrl);
        }
        else if(sortKey == 2) {
            new UrlQueryTask().execute(topRatedUrl);
        }

    }

    @Override
    public void onClick(int position) {

        /*if(mToast!=null) {
            mToast.cancel();
        }
        String message = "Movie "+(position+1)+" selected";
        mToast.makeText(this, message, Toast.LENGTH_SHORT).show();*/

        Intent movieDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        movieDetailIntent.putExtra("EXTRA_MOVIE_ID", mMovieID.get(position));
        movieDetailIntent.putExtra("EXTRA_RATING_COUNT", mVoteCounts.get(position));
        movieDetailIntent.putExtra("EXTRA_RATING", mVoteAverage.get(position));
        movieDetailIntent.putExtra("EXTRA_TITLE", mTitle.get(position));
        movieDetailIntent.putExtra("EXTRA_POSTER", mPoster.get(position));
        movieDetailIntent.putExtra("EXTRA_BACKDROP", mBackdropPath.get(position));
        movieDetailIntent.putExtra("EXTRA_OVERVIEW", mOverview.get(position));
        movieDetailIntent.putExtra("EXTRA_RELEASE_DATE", mReleaseDate.get(position));
        startActivity(movieDetailIntent);

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

                    List<String> voteCountsList = new ArrayList<>();
                    List<String> idList = new ArrayList<>();
                    List<String> videoList = new ArrayList<>();
                    List<String> voteAverageList = new ArrayList<>();
                    List<String> titleList = new ArrayList<>();
                    List<String> popularityList = new ArrayList<>();
                    List<String> posterPathList = new ArrayList<>();
                    List<String> originalLanguageList = new ArrayList<>();
                    List<String> originalTitleList = new ArrayList<>();
                    List<String> genreIdsList = new ArrayList<>();
                    List<String> backdropPathList = new ArrayList<>();
                    List<String> adultList = new ArrayList<>();
                    List<String> overviewList = new ArrayList<>();
                    List<String> releaseDateList = new ArrayList<>();

                    int resultsLength = results.length();

                    for(int i = 0; i<resultsLength; i++) {
                        JSONObject focus = results.getJSONObject(i);

                        voteCountsList.add(focus.optString(VOTE_COUNT_KEY));
                        idList.add(focus.optString(ID_KEY));
                        videoList.add(focus.optString(VIDEO_KEY));
                        voteAverageList.add(focus.optString(VOTE_AVERAGE_KEY));
                        titleList.add(focus.optString(TITLE_KEY));
                        popularityList.add(focus.optString(POPULARITY_KEY));
                        posterPathList.add(focus.optString(POSTER_PATH_KEY));
                        originalLanguageList.add(focus.optString(ORIGINAL_LANGUAGE_KEY));
                        originalTitleList.add(focus.optString(ORIGINAL_TITLE_KEY));
                        genreIdsList.add(focus.optString(GENRE_IDS_KEY));
                        backdropPathList.add(focus.optString(BACKDROP_PATH_KEY));
                        adultList.add(focus.optString(ADULT_KEY));
                        overviewList.add(focus.optString(OVERVIEW_KEY));
                        releaseDateList.add(focus.optString(RELEASE_DATE_KEY));
                    }

                    /*
                    String voteCounts = android.text.TextUtils.join(", ", voteCountsList);
                    String id = android.text.TextUtils.join(", ", idList);
                    String video = android.text.TextUtils.join(", ", videoList);
                    String voteAverage = android.text.TextUtils.join(", ", voteAverageList);
                    String title = android.text.TextUtils.join(", ", titleList);
                    String popularity = android.text.TextUtils.join(", ", popularityList);
                    String posterPath = android.text.TextUtils.join(", ", posterPathList);
                    String originalLanguage = android.text.TextUtils.join(", ", originalLanguageList);
                    String originalTitle = android.text.TextUtils.join(", ", originalTitleList);
                    String genreIds = android.text.TextUtils.join(", ", genreIdsList);
                    String backdropPath = android.text.TextUtils.join(", ", backdropPathList);
                    String adult = android.text.TextUtils.join(", ", adultList);
                    String overview = android.text.TextUtils.join(", ", overviewList);
                    String releaseDate = android.text.TextUtils.join(", ", releaseDateList);
                    */

                    mMovieID = idList;
                    mVoteCounts = voteCountsList;
                    mVoteAverage = voteAverageList;
                    mTitle = titleList;
                    mPoster = posterPathList;
                    mBackdropPath = backdropPathList;
                    mOverview = overviewList;
                    mReleaseDate = releaseDateList;
                    mMovieAdapter.setData(posterPathList, voteAverageList, popularityList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private Menu menu1 = null;

    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //TextView view = (TextView) findViewById(R.id.action_sort_popularity);
        //view.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        menu1 = menu;

        //Set text color of "By Popularity" menu to colorAccent
        MenuItem item = menu.getItem(SORT_POPULARITY_INDEX);
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spanString.length(), 0);
        item.setTitle(spanString);

        //Set text color of "By Popularity" menu to colorAccent
        MenuItem item2 = menu.getItem(SORT_RATING_INDEX);
        SpannableString spanString2 = new SpannableString(item2.getTitle().toString());
        spanString2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString2.length(), 0);
        item2.setTitle(spanString2);

        //Set text color of "By Popularity" menu to colorAccent
        MenuItem item3 = menu.getItem(SORT_FAVORITE_INDEX);
        SpannableString spanString3 = new SpannableString(item3.getTitle().toString());
        spanString3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString3.length(), 0);
        item3.setTitle(spanString3);

        return true;
    }

    private Menu getMenu() {
        return menu1;
    }

    //When menu clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_sort_popularity) {

            //Set text color of "Popular" menu to colorAccent
            SpannableString spanString = new SpannableString(item.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spanString.length(), 0);
            item.setTitle(spanString);

            //Set text color of "Top Rated" menu to white
            MenuItem item2 = getMenu().getItem(SORT_RATING_INDEX);
            SpannableString spanString2 = new SpannableString(item2.getTitle().toString());
            spanString2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString2.length(), 0);
            item2.setTitle(spanString2);

            //Set text color of "Favorite" menu to white
            MenuItem item3 = getMenu().getItem(SORT_FAVORITE_INDEX);
            SpannableString spanString3 = new SpannableString(item3.getTitle().toString());
            spanString3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString3.length(), 0);
            item3.setTitle(spanString3);

            makeUrlQuery(SORT_POPULARITY_KEY);
            return true;
        }
        else if (itemThatWasClickedId == R.id.action_sort_rating) {

            //Set text color of "Top Rated" menu to colorAccent
            SpannableString spanString = new SpannableString(item.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spanString.length(), 0);
            item.setTitle(spanString);

            //Set text color of "Popular" menu to white
            MenuItem item2 = getMenu().getItem(SORT_POPULARITY_INDEX);
            SpannableString spanString2 = new SpannableString(item2.getTitle().toString());
            spanString2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString2.length(), 0);
            item2.setTitle(spanString2);

            //Set text color of "Favorite" menu to white
            MenuItem item3 = getMenu().getItem(SORT_FAVORITE_INDEX);
            SpannableString spanString3 = new SpannableString(item3.getTitle().toString());
            spanString3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString3.length(), 0);
            item3.setTitle(spanString3);

            makeUrlQuery(SORT_RATING_KEY);
            return true;
        }

        else if (itemThatWasClickedId == R.id.action_sort_favorite) {

            //Set text color of "Favorite" menu to colorAccent
            SpannableString spanString = new SpannableString(item.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spanString.length(), 0);
            item.setTitle(spanString);

            //Set text color of "Popular" menu to white
            MenuItem item2 = getMenu().getItem(SORT_POPULARITY_INDEX);
            SpannableString spanString2 = new SpannableString(item2.getTitle().toString());
            spanString2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString2.length(), 0);
            item2.setTitle(spanString2);

            //Set text color of "Top Rated" menu to white
            MenuItem item3 = getMenu().getItem(SORT_RATING_INDEX);
            SpannableString spanString3 = new SpannableString(item3.getTitle().toString());
            spanString3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spanString3.length(), 0);
            item3.setTitle(spanString3);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
