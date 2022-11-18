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

import com.example.myapplication.R;
import com.example.myapplication.activity.Video_Detail_Activity;
import com.example.myapplication.bean.Fruit;

import java.util.List;

public class White_Noise_Adapter extends RecyclerView.Adapter<White_Noise_Adapter.ViewHolder> {
    private List<Fruit> mFruitList;
    private Context mContext;

    public White_Noise_Adapter(List<Fruit> mFruitList, Context context) {
        this.mFruitList = mFruitList;
        this.mContext = context;
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
        Fruit fruit = mFruitList.get(position);
        holder.fruitImage.setImageResource(fruit.getImageId());
        holder.fruitName.setText(fruit.getName());

        holder.lin_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, Video_Detail_Activity.class));
            }
        });
    }

    @Override
    public int getItemCount(){
        return mFruitList.size();
    }
}