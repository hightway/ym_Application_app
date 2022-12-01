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
import com.example.myapplication.R;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.plmd.Radio_Click_Set;

import java.util.ArrayList;
import java.util.List;

public class More_Radio_Adapter extends RecyclerView.Adapter<More_Radio_Adapter.ViewHolder> {
    private List<Hor_DateBean.DataBean.ListBean> list = new ArrayList<>();
    private Context mContext;
    public int index;

    public More_Radio_Adapter(Context mContext) {
        this.mContext = mContext;
        index = 0;
    }

    public void setDataList(List<Hor_DateBean.DataBean.ListBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void clean() {
        this.list.clear();
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.more_radio_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Hor_DateBean.DataBean.ListBean dataBean = list.get(position);

        if(dataBean != null){
            //设置内容
            viewHolder.txt_name.setText(dataBean.title);
            Glide.with(mContext)
                    .load(dataBean.icon)
                    .apply(RequestOptions
                            .bitmapTransform(new RoundedCorners(16))
                            .error(R.mipmap.loading_icon)
                            .placeholder(R.mipmap.loading_icon))
                    .into(viewHolder.img_bg);

            if(dataBean.anchors != null){
                viewHolder.txt_msg.setText(dataBean.anchors.anchor_name);
                Glide.with(mContext)
                        .load(dataBean.anchors.anchor_avatar)
                        .apply(RequestOptions
                                .bitmapTransform(new CircleCrop())
                                .error(R.mipmap.loading_icon)
                                .placeholder(R.mipmap.loading_icon))
                        .into(viewHolder.img_icon);
            }

            viewHolder.itemView.setTag(dataBean);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Hor_DateBean.DataBean.ListBean bean = (Hor_DateBean.DataBean.ListBean) view.getTag();
                    Radio_Click_Set.set_Click(bean);
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
        TextView txt_name;
        TextView txt_msg;
        FillImageView img_bg;
        ImageView img_icon;
        RelativeLayout rel_item;

        public ViewHolder(View itemView) {
            super(itemView);
            img_bg = itemView.findViewById(R.id.img_bg);
            txt_name = itemView.findViewById(R.id.txt_name);
            rel_item = itemView.findViewById(R.id.rel_item);
            img_icon = itemView.findViewById(R.id.img_icon);
            txt_msg = itemView.findViewById(R.id.txt_msg);
        }
    }
}
