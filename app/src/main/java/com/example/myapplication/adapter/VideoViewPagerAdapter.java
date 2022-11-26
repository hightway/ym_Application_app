package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
        boolean is_same = false;
        if(videoUrls != null && videoUrls.size() > 0){
            Video_Detail_Bean.DataBean.DetailBean detailBean = videoUrls.get(0);
            if(detailBean != null && mVieoUrls.size() > 0){
                for(Video_Detail_Bean.DataBean.DetailBean bean : mVieoUrls){
                    if(bean.id.equals(detailBean.id)){
                        is_same = true;
                        break;
                    }
                }
            }

            if(!is_same){
                mVieoUrls.add(detailBean);
                notifyItemInserted(getItemCount());
                //notifyDataSetChanged();
            }
        }
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
            Glide.with(mContext)
                    .load(detailBean.icon)
                    .into(holder.img_video_pic);

            holder.roll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exoPlayerView_click.view_vlick();
                }
            });

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
        public ImageView start;
        public FrameLayout roll;
        public ImageView img_video_pic;
        //public String videoUrl;

        VideoViewHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_view);
            img_video_pic = itemView.findViewById(R.id.img_video_pic);
            start = itemView.findViewById(R.id.start);
            roll = itemView.findViewById(R.id.roll);
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

    public Video_Detail_Bean.DataBean.DetailBean get_Radio(int pos) {
        return mVieoUrls.get(pos);
    }


    private ExoPlayerView_Click exoPlayerView_click;
    public void setExoPlayerView_Click(ExoPlayerView_Click exoPlayerView_click){
        this.exoPlayerView_click = exoPlayerView_click;
    }
    public interface ExoPlayerView_Click{
        void view_vlick();
    }

}
