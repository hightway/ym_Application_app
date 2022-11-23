package com.example.myapplication.adapter;

import android.content.Context;
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
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class Radio_CommonAdapter extends RecyclerView.Adapter<Radio_CommonAdapter.Re_ViewHolder>  {

    private Context mContext;

    private List<Hor_DateBean.DataBean.ListBean> mList = new ArrayList<>();

    public Radio_CommonAdapter(Context context) {
        mContext = context;
    }

    public void setGridDataList(List<Hor_DateBean.DataBean.ListBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void clean() {
        this.mList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Re_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_radio_item, parent, false);
        return new Re_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Re_ViewHolder holder, final int position) {
        Hor_DateBean.DataBean.ListBean data = mList.get(position);
        if(data != null){

            //加载图片
            Glide.with(mContext)
                    .load(data.icon)
                    .apply(RequestOptions
                            .bitmapTransform(new RoundedCorners(16))
                            .error(R.mipmap.loading_icon)
                            .placeholder(R.mipmap.loading_icon))
                    .into(holder.img_bg);

            holder.txt_name.setText(data.title);
            if(data.anchors != null){
                holder.tx_author.setText("- " + data.anchors.anchor_name);
            }

            holder.lin_tag.removeAllViews();
            List<Hor_DateBean.DataBean.TopBean.TagsBean> tagsBeans = data.tags;
            if(tagsBeans != null && tagsBeans.size() > 0){
                for(Hor_DateBean.DataBean.TopBean.TagsBean tagsBean : tagsBeans){
                    View add_view = LayoutInflater.from(mContext).inflate(R.layout.add_tx_view, holder.lin_tag, false);
                    TextView txt_msg = add_view.findViewById(R.id.txt_msg);
                    txt_msg.setText(tagsBean.name);
                    holder.lin_tag.addView(add_view);
                }
            }

            /*String id = data.getStationId();
            holder.lin_root.setTag(id);
            holder.lin_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 点击事件
                    //mContext.startActivity(new Intent(mContext, Charge_Station_DetailActivity.class).putExtra("gas_id", id));
                    String id = (String) view.getTag();
                    if(callback != null){
                        callback.charge_id(id);
                    }
                }
            });*/

        }
    }


    private Charge_Callback callback;
    public void setCharge_Callback(Charge_Callback charge_callback){
        this.callback = charge_callback;
    }
    public interface Charge_Callback{
        void charge_id(String id);
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class Re_ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name;
        public TextView tx_author;
        public FillImageView img_bg;
        public LinearLayout lin_tag;

        public Re_ViewHolder(View itemView) {
            super(itemView);
            img_bg = itemView.findViewById(R.id.img_bg);
            txt_name = itemView.findViewById(R.id.txt_name);
            tx_author = itemView.findViewById(R.id.tx_author);
            lin_tag = itemView.findViewById(R.id.lin_tag);
        }
    }

}
