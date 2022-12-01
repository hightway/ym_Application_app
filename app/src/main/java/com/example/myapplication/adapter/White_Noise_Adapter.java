package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.activity.Video_Detail_Activity;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Video_Info_Bean;
import com.example.myapplication.bean.White_Noise_Bean;
import com.example.myapplication.plmd.White_Noise_Cliack_Set;
import com.example.myapplication.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class White_Noise_Adapter extends RecyclerView.Adapter<White_Noise_Adapter.ViewHolder> {

    private List<White_Noise_Bean.DataBean> mFruitList = new ArrayList<>();
    private Context mContext;

    public White_Noise_Adapter(Context context) {
        this.mContext = context;
    }

    public void setDataList(List<White_Noise_Bean.DataBean> list) {
        this.mFruitList = list;
        notifyDataSetChanged();
    }

    public void clean() {
        this.mFruitList.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fruitImage;
        TextView fruitName;
        LinearLayout lin_root;

        public ViewHolder(@NonNull View view) {
            super(view);
            fruitImage = (ImageView)view.findViewById(R.id.fruit_image);
            fruitName = (TextView)view.findViewById(R.id.fruit_name);
            lin_root = (LinearLayout)view.findViewById(R.id.lin_root);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noise_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        White_Noise_Bean.DataBean fruit = mFruitList.get(position);
        Glide.with(mContext)
                .load(fruit.icon)
                .apply(RequestOptions
                        .bitmapTransform(new RoundedCorners(16))
                        .error(R.mipmap.loading_icon)
                        .placeholder(R.mipmap.loading_icon))
                .into(holder.fruitImage);
        holder.fruitName.setText(fruit.title);

        holder.lin_root.setTag(fruit);
        holder.lin_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                White_Noise_Bean.DataBean dataBean = (White_Noise_Bean.DataBean) view.getTag();
                White_Noise_Cliack_Set.set_noise_click(dataBean);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mFruitList.size();
    }

}