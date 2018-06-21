package com.example.android.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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

        final Context context = holder.videoNameView.getContext();

        URL imageURL = NetworkUtils.buildVideoImageUrl(mVideoKey.get(position));

        Picasso.with(context)
                .load(imageURL.toString())
                .placeholder(R.drawable.placeholder_backdrop)
                .error(R.drawable.no_image_backdrop)
                .into(holder.videoThumbnailView);

        /*
        int imageWidth = holder.videoThumbnailView.getDrawable().getIntrinsicWidth();
        int imageHeight = holder.videoThumbnailView.getDrawable().getIntrinsicHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManger = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManger.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        */

        ViewGroup.LayoutParams params = holder.videoThumbnailView.getLayoutParams();
        params.width = 640;
        params.height = 360;
        holder.videoThumbnailView.setLayoutParams(params);

        final URL youtubeURL = NetworkUtils.buildYouTubeUrl(mVideoKey.get(position));

        holder.videoThumbnailView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL.toString())));
                Log.i("Video", "Video Playing....");
            }
        });

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

        final TextView videoNameView;
        final ImageView videoThumbnailView;

        final TextView videoLabelView;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoNameView = itemView.findViewById(R.id.detail_video_name);
            videoThumbnailView = itemView.findViewById(R.id.detail_video_thumbnail);

            videoLabelView = itemView.findViewById(R.id.detail_video_label);

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
