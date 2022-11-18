package com.example.myapplication.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.bean.Hor_DateBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class Anchor_Radio_CommonAdapter extends CommonAdapter<Hor_DateBean> {

    //参1上下文
    public Anchor_Radio_CommonAdapter(Context context, int layoutId, List<Hor_DateBean> datas) {
        super(context, layoutId, datas);
    }

    //设置数据
    @Override
    protected void convert(ViewHolder holder, Hor_DateBean appInfo, int position) {
        //设置数据
        /*holder.setText(R.id.tv_title, appInfo.name)
                .setText(R.id.tv_size, Formatter.formatFileSize(holder.itemView.getContext(), appInfo.size))
                .setText(R.id.tv_des, appInfo.des)
                .setRating(R.id.rb_star, appInfo.stars);*/

        //加载图片
        Glide.with(holder.itemView.getContext())
                .load(R.mipmap.wx_icon)
                .into((ImageView) holder.getView(R.id.img_bg));

        /*Glide.with(holder.itemView.getContext())
                .load(Url.ImagePrefix + appInfo.iconUrl)
                .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext()))
                .placeholder(R.drawable.ic_default)//默认的图片
                .error(R.mipmap.ic_launcher)//加载失败显示的图片
                .crossFade(1000)//渐渐显示出来的时间
                //找到view--getview(id)
                .into((ImageView) holder.getView(R.id.iv_icon));*/

    }
}