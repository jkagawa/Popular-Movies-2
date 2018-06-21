package com.example.android.popularmovies2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by Joshua on 6/8/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    //final private ReviewAdapter.ReviewAdapterOnClickHandler mClickHandler;
    public final int mPosition;

    private List<String> mReviewAuthor;
    private List<String> mReviewContent;

    //Context context;
    //final String mystring = context.getResources().getString(R.string.dash);

    /*public interface ReviewAdapterOnClickHandler {
        void onClick(int position);
    }*/

    public ReviewAdapter(int position) {
        mPosition = position;
        //mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.review_list_item;


        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewAdapter.ReviewViewHolder holder, int position) {

        holder.reviewAuthorView.setText(mReviewAuthor.get(position));
        holder.reviewContentView.setText(mReviewContent.get(position));

    }

    @Override
    public int getItemCount() {
        if(mReviewContent==null) {

            return 0;
        }
        else {
            return mReviewContent.size();
        }
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView reviewAuthorView;
        final TextView reviewContentView;

        final TextView reviewLabelView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewAuthorView = itemView.findViewById(R.id.detail_review_author);
            reviewContentView = itemView.findViewById(R.id.detail_review);

            reviewLabelView = itemView.findViewById(R.id.detail_review_label);

            //itemView.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.onClick(clickedPosition);
        }*/

    }

    public void setData(List<String> reviewAuthorList, List<String> reviewContentList) {
        mReviewAuthor = reviewAuthorList;
        mReviewContent = reviewContentList;
        notifyDataSetChanged();
    }
}
