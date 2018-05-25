package com.example.android.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Joshua on 5/24/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private ImageView mPoster;
    private ImageView mBackdrop;

    private TextView mVoteCounts;
    private TextView mVoteAverage;
    private TextView mOverview;
    private TextView mReleaseDate;

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
        }
    }

}
