package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.myapplication.bean.TagsBean;
import com.example.myapplication.bean.Video_History_Bean;
import com.example.myapplication.custom.FillImageView;
import com.example.myapplication.plmd.Radio_Click_Set;

import java.util.ArrayList;
import java.util.List;

public class WhiteNoise_History_Adapter extends RecyclerView.Adapter<WhiteNoise_History_Adapter.Re_ViewHolder>  {

    private Context mContext;

    private List<Video_History_Bean.DataBean> mList = new ArrayList<>();

    public WhiteNoise_History_Adapter(Context context) {
        mContext = context;
    }

    public void setGridDataList(List<Video_History_Bean.DataBean> list) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.wn_history_item, parent, false);
        return new Re_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Re_ViewHolder holder, final int position) {
        Video_History_Bean.DataBean data = mList.get(position);
        if(data != null){
            holder.txt_name.setText(data.title);
            holder.tx_index.setText(String.valueOf(position+1));

            holder.lin_tag.removeAllViews();
            List<TagsBean> tagsBeans = data.tagsList;
            if(tagsBeans != null && tagsBeans.size() > 0){
                for(TagsBean tagsBean : tagsBeans){
                    View add_view = LayoutInflater.from(mContext).inflate(R.layout.add_tx_view, holder.lin_tag, false);
                    TextView txt_msg = add_view.findViewById(R.id.txt_msg);
                    txt_msg.setText(tagsBean.name);
                    holder.lin_tag.addView(add_view);
                }
            }

            holder.rel_item.setTag(data);
            holder.rel_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 点击事件
                    Video_History_Bean.DataBean bean = (Video_History_Bean.DataBean) view.getTag();
                    if(whiteNoise_history_callBack != null){
                        whiteNoise_history_callBack.whitenoise_click(bean);
                    }
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class Re_ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name;
        public TextView tx_index;
        public LinearLayout lin_tag;
        public RelativeLayout rel_item;

        public Re_ViewHolder(View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            tx_index = itemView.findViewById(R.id.tx_index);
            lin_tag = itemView.findViewById(R.id.lin_tag);
            rel_item = itemView.findViewById(R.id.rel_item);
        }
    }


    private WhiteNoise_History_callBack whiteNoise_history_callBack;
    public void setWhiteNoise_History_callBack(WhiteNoise_History_callBack callBack){
        this.whiteNoise_history_callBack = callBack;
    }
    public interface WhiteNoise_History_callBack{
        void whitenoise_click(Video_History_Bean.DataBean bean);
    }

}