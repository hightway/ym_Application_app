package com.example.myapplication.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.bean.White_Noise_Bean;
import com.example.myapplication.tools.Utils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class Anchor_Radio_CommonAdapter extends CommonAdapter<Hor_DateBean.DataBean.ListBean> {

    //参1上下文
    public Anchor_Radio_CommonAdapter(Context context, int layoutId, List<Hor_DateBean.DataBean.ListBean> datas) {
        super(context, layoutId, datas);
    }

    /*public void setDataList(List<Hor_DateBean.DataBean.ListBean> list) {
        this.mDatas = list;
        notifyDataSetChanged();
    }*/

    public void addData(List<Hor_DateBean.DataBean.ListBean> listBeans) {
        getDatas().addAll(listBeans);
        notifyDataSetChanged();
    }

    public void clean() {
        this.mDatas.clear();
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    protected void convert(ViewHolder holder, Hor_DateBean.DataBean.ListBean appInfo, int position) {
        //设置数据
        /*holder.setText(R.id.tv_title, appInfo.name)
                .setText(R.id.tv_size, Formatter.formatFileSize(holder.itemView.getContext(), appInfo.size))
                .setText(R.id.tv_des, appInfo.des)
                .setRating(R.id.rb_star, appInfo.stars);*/
        //加载图片
        Glide.with(mContext)
                .load(appInfo.icon)
                .apply(RequestOptions
                        .bitmapTransform(new RoundedCorners(16))
                        .error(R.mipmap.loading_icon)
                        .placeholder(R.mipmap.loading_icon))
                .into((ImageView) holder.getView(R.id.img_bg));

        holder.setText(R.id.txt_name, appInfo.title)
                .setText(R.id.tx_author, "- " + appInfo.anchors.anchor_name);
    }
}