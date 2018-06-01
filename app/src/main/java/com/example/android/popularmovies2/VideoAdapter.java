package com.example.android.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by Joshua on 5/31/2018.
 */

class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    final private VideoAdapterOnClickHandler mClickHandler;
    public final int mPosition;

    private List<String> mVideoKey;
    private List<String> mVideoNameList;

    public interface VideoAdapterOnClickHandler {
        void onClick(int position);
    }

    public VideoAdapter(int position, VideoAdapterOnClickHandler clickHandler) {
        mPosition = position;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.video_list_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, int position) {

        //Context context = holder.videoKeyView.getContext();

        URL youtubeURL = NetworkUtils.buildYouTubeUrl(mVideoKey.get(position));

        /*
        videoKeyView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL)));
                Log.i("Video", "Video Playing....");
            }
        });
        */

        holder.videoNameView.setText(mVideoNameList.get(position));

    }

    @Override
    public int getItemCount() {
        if(mVideoKey==null) {
            return 0;
        }
        else {
            return mVideoKey.size();
        }
    }


    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //final Button videoKeyView;
        final TextView videoNameView;

        public VideoViewHolder(View itemView) {
            super(itemView);

            //videoKeyView = itemView.findViewById(R.id.button);
            videoNameView = itemView.findViewById(R.id.detail_video_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.onClick(clickedPosition);
        }

    }

    public void setData(List<String> videoKeyList, List<String> videoNameList) {
        mVideoKey = videoKeyList;
        mVideoNameList = videoNameList;
        notifyDataSetChanged();
    }


}
