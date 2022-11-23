package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.tools.Utils;

import java.util.List;

public class HosGridViewAdapter extends RecyclerView.Adapter<HosGridViewAdapter.ViewHolder> {
    private List<Hor_DateBean.DataBean.TopBean> list;
    private Context mContext;
    public int index;
    public OnRvItemClick onRvItemClick;

    public HosGridViewAdapter(List<Hor_DateBean.DataBean.TopBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        index = 0;
    }

    public void setOnHosGridItemClick(OnRvItemClick onRvItemClick) {
        this.onRvItemClick = onRvItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hor_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        //设置内容
        Glide.with(mContext)
                .load(list.get(position).icon)
                .apply(RequestOptions
                        .bitmapTransform(new RoundedCorners(24))
                        .error(R.mipmap.loading_pic)
                        .placeholder(R.mipmap.loading_pic))
                .into(viewHolder.img_bg);

        viewHolder.txt_name.setText(list.get(position).title);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRvItemClick.OnRvItemClick(v);
            }
        });
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
        TextView txt_name;
        FillImageView img_bg;
        RelativeLayout rel_item;

        public ViewHolder(View itemView) {
            super(itemView);
            img_bg = itemView.findViewById(R.id.img_bg);
            txt_name = itemView.findViewById(R.id.txt_name);
            rel_item = itemView.findViewById(R.id.rel_item);
        }
    }

    public interface OnRvItemClick {
        void OnRvItemClick(View view);
    }
}