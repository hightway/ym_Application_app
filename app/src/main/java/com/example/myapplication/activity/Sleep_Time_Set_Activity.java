package com.example.myapplication.activity;

import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.DatePickerAdapter;
import com.example.myapplication.custom.ScrollPickerView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Sleep_Time_Set_Activity extends BaseActivity implements ScrollPickerView.OnItemSelectedListener{

    private Sleep_Time_Set_Activity instance;

    @BindView(R.id.query_button)
    ImageView query_button;
    @BindView(R.id.datepicker_year_1)
    ScrollPickerView datepicker_year_1;
    @BindView(R.id.datepicker_year_2)
    ScrollPickerView datepicker_year_2;
    private DatePickerAdapter mDayAdapter;
    private DatePickerAdapter mAdapter;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.sleep_set_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        mDayAdapter = new DatePickerAdapter(0, 23, new DecimalFormat("00"));
        mAdapter = new DatePickerAdapter(0, 59, new DecimalFormat("00"));

        datepicker_year_1.setAdapter(mDayAdapter);
        datepicker_year_2.setAdapter(mAdapter);

        datepicker_year_1.setOnItemSelectedListener(this);
        datepicker_year_2.setOnItemSelectedListener(this);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.query_button)
    public void query_button(){
        finish();
    }


    @Override
    public void onItemSelected(View view, int position) {
        switch (view.getId()) {
            case R.id.datepicker_year_1: {
                int date = mDayAdapter.getDate(position);
                //resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            case R.id.datepicker_year_2: {
                int date = mAdapter.getDate(position);
                //resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            default:{
                break;
            }
        }
    }


    /*private void resetMaxDay() {
        int newMaxDay = getMaxDay(mSelectedYear, mSelectedMonth);
        if (newMaxDay != mDayAdapter.getMaxValue()) {
            mDayAdapter.setMaxValue(newMaxDay);
            datepicker_year_2.invalidate();
        }
    }*/

    /*private int getMaxDay(int year, int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
        return 31;
    }*/

}
