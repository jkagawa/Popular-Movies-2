package com.example.android.popularmovies2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by Joshua on 5/24/2018.
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    final private MovieAdapterOnClickHandler mClickHandler;
    public final int mPosition;

    private List<String> mPoster;
    private List<String> mRating;

    public interface MovieAdapterOnClickHandler {
        void onClick(int position);
    }

    public MovieAdapter(int position, MovieAdapterOnClickHandler clickHandler) {
        mPosition = position;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_list_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {

        Context context = holder.posterView.getContext();

        URL imageURL = NetworkUtils.buildImageUrl(mPoster.get(position),"w780");

        Picasso.with(context)
                .load(imageURL.toString())
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.no_image_poster)
                .into(holder.posterView);

        holder.ratingView.setText(mRating.get(position));

    }

    @Override
    public int getItemCount() {
        if(mRating==null) {
            return 0;
        }
        else {
            return mRating.size();
        }
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView posterView;
        final TextView ratingView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            posterView = itemView.findViewById(R.id.movie_poster);
            ratingView = itemView.findViewById(R.id.rating);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.onClick(clickedPosition);
        }

    }

    public void setData(List<String> posterPath, List<String> voteAverageList, List<String> popularityList) {
        mPoster = posterPath;
        mRating = voteAverageList;
        notifyDataSetChanged();
    }


}

