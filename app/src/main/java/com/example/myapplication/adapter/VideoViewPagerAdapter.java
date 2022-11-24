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

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.bean.Video_Detail_Bean;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;
import java.util.List;

public class VideoViewPagerAdapter extends RecyclerView.Adapter<VideoViewPagerAdapter.VideoViewHolder> {

    private Context mContext;
    private List<Video_Detail_Bean.DataBean.DetailBean> mVieoUrls = new ArrayList<>();


    public VideoViewPagerAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setDataList(List<Video_Detail_Bean.DataBean.DetailBean> videoUrls) {
        mVieoUrls.clear();
        mVieoUrls.addAll(videoUrls);
        notifyDataSetChanged();
    }

    public void addDataList(List<Video_Detail_Bean.DataBean.DetailBean> videoUrls) {
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
        Video_Detail_Bean.DataBean.DetailBean detailBean = mVieoUrls.get(position);
        if(detailBean != null){
            /*Glide.with(mContext)
                    .load(detailBean.icon)
                    .into(holder.img_video_pic);*/

            //holder.videoUrl = mVieoUrls.get(position);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return mVieoUrls.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public SimpleExoPlayerView mVideoView;
        public SurfaceView surface_view;
        public ImageView start;
        public ImageView img_video_pic;
        //public String videoUrl;

        VideoViewHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_view);
            img_video_pic = itemView.findViewById(R.id.img_video_pic);
            start = itemView.findViewById(R.id.start);
            /*surface_view = itemView.findViewById(R.id.surface_view);
            start = itemView.findViewById(R.id.start);*/
        }
    }

    public String getUrlByPos(int pos) {
        return mVieoUrls.get(pos).resource_url;
    }

    public int get_id(int pos) {
        return mVieoUrls.get(pos).id;
    }

    public Video_Detail_Bean.DataBean.DetailBean get_Bean(int pos) {
        return mVieoUrls.get(pos);
    }

}
