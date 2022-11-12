package com.example.myapplication.fragment;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.Sleep_Time_Set_Activity;
import com.example.myapplication.base.BaseLazyFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_3 extends BaseLazyFragment{

    @BindView(R.id.tx_sleep_time)
    TextView tx_sleep_time;

    @Override
    protected int setLayout() {
        return R.layout.tab_3_fragment_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }


    @OnClick(R.id.tx_sleep_time)
    public void tx_sleep_time(){
        startActivity(new Intent(getActivity(), Sleep_Time_Set_Activity.class));
    }


}