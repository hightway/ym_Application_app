package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.activity.Video_Detail_Activity;
import com.example.myapplication.bean.Video_Detail_Bean;
import com.example.myapplication.bean.Video_History_Bean;
import com.example.myapplication.bean.Video_Info_Bean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.http.Api;
import com.example.myapplication.tools.Aliyun_Login_Util;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.OkHttpUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Video_History_Adapter extends RecyclerView.Adapter<Video_History_Adapter.ViewHolder> {
    private List<Video_History_Bean.DataBean> mFruitList = new ArrayList<>();
    private Context mContext;

    public Video_History_Adapter(Context context) {
        this.mContext = context;
    }

    public void setGridDataList(List<Video_History_Bean.DataBean> list) {
        this.mFruitList = list;
        notifyDataSetChanged();
    }

    public void clean() {
        this.mFruitList.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        FillImageView fruitImage;
        TextView fruitName;
        LinearLayout lin_root;

        public ViewHolder(@NonNull View view) {
            super(view);
            fruitImage = view.findViewById(R.id.fruit_image);
            fruitName = view.findViewById(R.id.fruit_name);
            lin_root = view.findViewById(R.id.lin_root);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video_History_Bean.DataBean fruit = mFruitList.get(position);
        holder.fruitName.setText(fruit.title);
        if(!TextUtils.isEmpty(fruit.resource_icon)){
            Glide.with(mContext)
                    .load(fruit.resource_icon)
                    .apply(RequestOptions
                            .bitmapTransform(new RoundedCorners(24))
                            .error(R.mipmap.loading_pic)
                            .placeholder(R.mipmap.loading_pic))
                    .into(holder.fruitImage);
        }

        holder.lin_root.setTag(fruit.resource_id);
        holder.lin_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_detail(mContext, (Integer) view.getTag());
            }
        });
    }


    @Override
    public int getItemCount(){
        return mFruitList.size();
    }


    private void get_detail(Context instance, int detail_id) {
        DialogUtils.getInstance().showDialog(instance, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(detail_id));
        OkHttpUtil.postRequest(Api.HEAD + "featured_video/detail", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                if(!TextUtils.isEmpty(err) && err.contains("401")){
                    //未登录，看详情需要登录
                    Aliyun_Login_Util.getInstance().initSDK((Activity) instance);
                }
            }

            @Override
            public void un_login_err() {
                //未登录，看详情需要登录
                Aliyun_Login_Util.getInstance().initSDK((Activity) instance);
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Video_Detail_Bean video_detail_bean = new Gson().fromJson(response, Video_Detail_Bean.class);
                        mContext.startActivity(new Intent(mContext, Video_Detail_Activity.class).putExtra("detail_bean", video_detail_bean));
                    }else{
                        Toast.makeText(instance, jsonObject.getString("errMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}