package com.example.myapplication.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.bean.His_DateBean;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.bean.TagsBean;
import com.example.myapplication.bean.White_Noise_Bean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.custom.ImageRound;
import com.example.myapplication.plmd.Radio_Click_Set;
import com.example.myapplication.plmd.White_Noise_Cliack_Set;

import java.util.ArrayList;
import java.util.List;

public class Play_History_Adapter extends RecyclerView.Adapter<Play_History_Adapter.ViewHolder> {
    private List<His_DateBean.DataBean> list = new ArrayList<>();
    private Context mContext;
    public int index;

    public Play_History_Adapter(Context mContext) {
        this.mContext = mContext;
        index = 0;
    }

    public void setGridDataList(List<His_DateBean.DataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void clean() {
        this.list.clear();
        notifyDataSetChanged();
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
        His_DateBean.DataBean dataBean = list.get(position);
        if(dataBean != null){
            Glide.with(mContext)
                    .load(dataBean.resource_icon)
                    .apply(RequestOptions
                            .bitmapTransform(new CircleCrop())
                            .error(R.mipmap.loading_icon)
                            .placeholder(R.mipmap.loading_icon))
                    .into(viewHolder.img_bg);

            holder.txt_name.setText(dataBean.title);

            holder.lin_tag.removeAllViews();
            List<TagsBean> tagsBeans = dataBean.tags;
            if(tagsBeans != null && tagsBeans.size() > 0){
                for(TagsBean tagsBean : tagsBeans){
                    View add_view = LayoutInflater.from(mContext).inflate(R.layout.add_tx_view, holder.lin_tag, false);
                    TextView txt_msg = add_view.findViewById(R.id.txt_msg);
                    txt_msg.setText(tagsBean.name);
                    holder.lin_tag.addView(add_view);
                }
            }

            String type = dataBean.resource_type;
            if(!TextUtils.isEmpty(type) && type.equals("radio_stations")){
                holder.txt_msg.setText(dataBean.anchor_name);

                holder.img_avatar.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(dataBean.anchor_avatar)
                        .apply(RequestOptions
                                .bitmapTransform(new RoundedCorners(16))
                                .error(R.mipmap.loading_icon)
                                .placeholder(R.mipmap.loading_icon))
                        .into(holder.img_avatar);
            }else{
                holder.img_avatar.setVisibility(View.GONE);
                holder.txt_msg.setText(mContext.getString(R.string.noise));
            }

            viewHolder.itemView.setTag(dataBean);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    His_DateBean.DataBean dataBean = (His_DateBean.DataBean) v.getTag();
                    if(dataBean.resource_type.equals("radio_stations")){
                        Hor_DateBean.DataBean.ListBean listBean = getRadio_Bean(dataBean);
                        Radio_Click_Set.set_Click(listBean);
                    }else{
                        White_Noise_Bean.DataBean noise_bean = getNoise_Bean(dataBean);
                        White_Noise_Cliack_Set.set_noise_click(noise_bean);
                    }
                }
            });
        }
    }

    private White_Noise_Bean.DataBean getNoise_Bean(His_DateBean.DataBean dataBean) {
        White_Noise_Bean.DataBean bean = new White_Noise_Bean.DataBean();
        bean.id = dataBean.resource_id;
        bean.title = dataBean.title;
        bean.resource_duration = dataBean.resource_duration;
        bean.resource_type = dataBean.resource_type;
        bean.resource_url = "";
        bean.icon = dataBean.resource_icon;
        bean.updated_at = dataBean.updated_at;
        bean.tags = dataBean.tags;
        return bean;
    }

    private Hor_DateBean.DataBean.ListBean getRadio_Bean(His_DateBean.DataBean dataBean) {
        Hor_DateBean.DataBean.ListBean bean = new Hor_DateBean.DataBean.ListBean();
        bean.id = dataBean.resource_id;
        bean.title = dataBean.title;
        bean.resource_duration = dataBean.resource_duration;
        bean.resource_type = dataBean.resource_type;
        bean.resource_url = "";
        bean.icon = dataBean.resource_icon;
        bean.updated_at = dataBean.updated_at;
        bean.tags = dataBean.tags;

        Hor_DateBean.DataBean.ListBean.AnchorsBean anchorsBean = new Hor_DateBean.DataBean.ListBean.AnchorsBean();
        anchorsBean.anchor_name = dataBean.anchor_name;
        anchorsBean.anchor_avatar = dataBean.anchor_avatar;
        anchorsBean.anchor_id = dataBean.anchor_id;
        bean.anchors = anchorsBean;
        return bean;
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
        ImageView img_bg;
        RelativeLayout rel_item;
        ImageRound img_avatar;
        LinearLayout lin_tag;

        public ViewHolder(View itemView) {
            super(itemView);
            img_bg = itemView.findViewById(R.id.img_bg);
            txt_msg = itemView.findViewById(R.id.txt_msg);
            txt_name = itemView.findViewById(R.id.txt_name);
            rel_item = itemView.findViewById(R.id.rel_item);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            lin_tag = itemView.findViewById(R.id.lin_tag);
        }
    }

}