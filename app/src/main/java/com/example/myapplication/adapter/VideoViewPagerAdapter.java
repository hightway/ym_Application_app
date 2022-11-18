package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;
import java.util.List;

public class VideoViewPagerAdapter extends RecyclerView.Adapter<VideoViewPagerAdapter.VideoViewHolder> {

    private Context mContext;
    private List<String> mVieoUrls = new ArrayList<>();


    public VideoViewPagerAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setDataList(List<String> videoUrls) {
        mVieoUrls.clear();
        mVieoUrls.addAll(videoUrls);
        notifyDataSetChanged();
    }

    public void addDataList(List<String> videoUrls) {
        mVieoUrls.addAll(videoUrls);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewPagerAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.fragment_video_item, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewPagerAdapter.VideoViewHolder holder, int position) {
        holder.videoUrl = mVieoUrls.get(position);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mVieoUrls.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public SimpleExoPlayerView mVideoView;
        public SurfaceView surface_view;
        public ImageView start;
        public String videoUrl;

        VideoViewHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_view);
            /*surface_view = itemView.findViewById(R.id.surface_view);
            start = itemView.findViewById(R.id.start);*/
        }
    }

    public String getUrlByPos(int pos) {
        return mVieoUrls.get(pos);
    }

}
