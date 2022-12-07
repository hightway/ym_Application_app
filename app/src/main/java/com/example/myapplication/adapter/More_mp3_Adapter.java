package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.bean.Raw_Bean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.custom.VoisePlayingIcon;
import com.example.myapplication.plmd.Radio_Click_Set;
import com.example.myapplication.tools.MediaUtil;

import java.util.ArrayList;
import java.util.List;

public class More_mp3_Adapter extends RecyclerView.Adapter<More_mp3_Adapter.ViewHolder> {
    private List<Raw_Bean> list = new ArrayList<>();
    private Context mContext;
    private int pos = -1;

    public More_mp3_Adapter(Context mContext, List<Raw_Bean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void sel_pos(int pos){
        this.pos = pos;
        notifyDataSetChanged();
    }

    public void stop_all(){
        this.pos = -1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.more_mp3_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Raw_Bean dataBean = list.get(position);

        if(dataBean != null){
            //设置内容
            viewHolder.txt_name.setText(dataBean.getRawName());
            viewHolder.txt_msg.setText(mContext.getResources().getString(R.string.time_long) + dataBean.getTime_duraton());
            Glide.with(mContext)
                    .load("http://")
                    .apply(RequestOptions
                            .bitmapTransform(new RoundedCorners(16))
                            .error(R.mipmap.loading_icon)
                            .placeholder(R.mipmap.loading_icon))
                    .into(viewHolder.text_num);

            if(pos == position){
                viewHolder.voise_icon.setVisibility(View.VISIBLE);
                viewHolder.voise_icon.start();
            }else{
                viewHolder.voise_icon.setVisibility(View.GONE);
                viewHolder.voise_icon.stop();
            }

            viewHolder.itemView.setTag(position);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    if(raw_onClick_callBack != null){
                        raw_onClick_callBack.Raw_click(pos);
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView text_num;
        TextView txt_name;
        TextView txt_msg;
        VoisePlayingIcon voise_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            text_num = itemView.findViewById(R.id.text_num);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_msg = itemView.findViewById(R.id.txt_msg);
            voise_icon = itemView.findViewById(R.id.voise_icon);
        }
    }

    private Raw_OnClick_CallBack raw_onClick_callBack;
    public void setRaw_OnClick_CallBack(Raw_OnClick_CallBack raw_onClick_callBack){
        this.raw_onClick_callBack = raw_onClick_callBack;
    }
    public interface Raw_OnClick_CallBack{
        void Raw_click(int pos);
    }

}