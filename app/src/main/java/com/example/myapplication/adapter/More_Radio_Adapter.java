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

import com.example.myapplication.R;
import com.example.myapplication.bean.His_DateBean;

import java.util.List;

public class More_Radio_Adapter extends RecyclerView.Adapter<More_Radio_Adapter.ViewHolder> {
    private List<His_DateBean> list;
    private Context mContext;
    public int index;
    public OnRvItemClick onRvItemClick;

    public More_Radio_Adapter(List<His_DateBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        index = 0;
    }

    public void setOnHosGridItemClick(OnRvItemClick onRvItemClick) {
        this.onRvItemClick = onRvItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        //设置内容
        viewHolder.img_bg.setImageResource(list.get(position).getImg());

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
        ImageView img_bg;
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
