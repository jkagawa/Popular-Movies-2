package com.example.android.popularmovies2.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Joshua on 5/24/2018.
 */

public class NetworkUtils {

    final static String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie";

    final static String POPULAR_PATH = "popular";

    final static String TOP_RATED_PATH = "top_rated";

    final static String VIDEO_PATH = "videos";

    final static String REVIEW_PATH = "reviews";

    final static String PARAM_API_KEY = "api_key";

    final static String YOUTUBE_URL = "https://www.youtube.com/watch";

    final static String PARAM_VIDEO = "v";

    //Build URL to query for popular movies
    public static URL buildPopularUrl(String apiKey) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(POPULAR_PATH)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //Build URL to query for top rated movies
    public static URL buildTopRatedUrl(String apiKey) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(TOP_RATED_PATH)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //Build URL for poster image
    public static URL buildImageUrl(String imagePath, String width) {
        imagePath = imagePath.replace("/", "");
        Uri builtUri = Uri.parse("https://image.tmdb.org/t/p/"+width).buildUpon()
                .appendPath(imagePath)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //Build URL to query for trailer videos
    public static URL buildVideoJSONUrl(String apiKey, String movieID) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //Build URL to query for reviews
    public static URL buildReviewJSONUrl(String apiKey, String movieID) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(REVIEW_PATH)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //Build URL for video trailers
    public static URL buildYouTubeUrl(String videoKey) {
        Uri builtUri = Uri.parse(YOUTUBE_URL).buildUpon()
                .appendQueryParameter(PARAM_VIDEO, videoKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //Search internet using the URL
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /*
    public static Bitmap bitmapFromUrl(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);
            return bitmapImage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
    */

}