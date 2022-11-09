package com.example.myapplication.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.custom.DatePickerAdapter;
import com.example.myapplication.custom.Item;
import com.example.myapplication.custom.ScrollPickerView;
import com.manu.mdatepicker.MDatePicker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.defaults.view.PickerView;

public class TabFragment_3 extends BaseLazyFragment implements ScrollPickerView.OnItemSelectedListener{


    @BindView(R.id.pickerView)
    PickerView pickerView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.datepicker_year)
    ScrollPickerView datepicker_year;
    @BindView(R.id.datepicker_year_1)
    ScrollPickerView datepicker_year_1;
    @BindView(R.id.datepicker_year_2)
    ScrollPickerView datepicker_year_2;
    private DatePickerAdapter mYearAdapter;
    private DatePickerAdapter mMonthAdapter;
    private DatePickerAdapter mDayAdapter;
    private int mSelectedYear;
    private int mSelectedMonth;
    private int mSelectedDay;

    @Override
    protected int setLayout() {
        return R.layout.tab_3_fragment_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);


        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            items.add(new Item("Item " + i));
        }

        pickerView.setItems(Item.sampleItems(), new PickerView.OnItemSelectedListener<Item>() {
            @Override
            public void onItemSelected(Item item) {
                textView.setText(item.getText());
            }
        });



        PickerView.Adapter adapter = new PickerView.Adapter() {

            @Override
            public int getItemCount() {
                return 42;
            }

            @Override
            public PickerView.PickerItem getItem(int index) {
                return items.get(index);
            }

            @Override
            public String getText(int index) {
                return "Item " + index;
            }
        };
        pickerView.setAdapter(adapter);



        pickerView.setOnSelectedItemChangedListener(new PickerView.OnSelectedItemChangedListener() {
            @Override
            public void onSelectedItemChanged(PickerView pickerView, int previousPosition, int selectedItemPosition) {
                textView.setText(pickerView.getAdapter().getText(selectedItemPosition));
            }
        });


        mYearAdapter = new DatePickerAdapter(1800, 2200, new DecimalFormat("0000"));
        mMonthAdapter = new DatePickerAdapter(1, 12, new DecimalFormat("00"));
        mDayAdapter = new DatePickerAdapter(1, 31, new DecimalFormat("00"));

        datepicker_year.setAdapter(mYearAdapter);
        datepicker_year_1.setAdapter(mMonthAdapter);
        datepicker_year_2.setAdapter(mDayAdapter);

        datepicker_year.setOnItemSelectedListener(this);
        datepicker_year_1.setOnItemSelectedListener(this);
        datepicker_year_2.setOnItemSelectedListener(this);

    }



    @OnClick(R.id.textView)
    public void setTextView(){
        MDatePicker.create(getActivity())
                //附加设置(非必须,有默认值)
                .setCanceledTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .setSupportTime(true)
                .setTwelveHour(false)
                //结果回调(必须)
                .setOnDateResultListener(new MDatePicker.OnDateResultListener() {
                    @Override
                    public void onDateResult(long date) {
                        // date
                    }
                })
                .build()
                .show();
    }


    @Override
    public void onItemSelected(View view, int position) {
        switch (view.getId()) {
            case R.id.datepicker_year: {
                mSelectedYear = mYearAdapter.getDate(position);
                resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            case R.id.datepicker_year_1: {
                mSelectedMonth = mMonthAdapter.getDate(position);
                resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            case R.id.datepicker_year_2: {
                mSelectedDay = mDayAdapter.getDate(position);
                break;
            }
            default:{
                break;
            }
        }
        textView.setText(mSelectedYear + "-" + mSelectedMonth + "-" + mSelectedDay);
    }



    private void resetMaxDay() {
        int newMaxDay = getMaxDay(mSelectedYear, mSelectedMonth);
        if (newMaxDay != mDayAdapter.getMaxValue()) {
            mDayAdapter.setMaxValue(newMaxDay);
            datepicker_year_2.invalidate();
        }
    }

    private int getMaxDay(int year, int month) {
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
    }


}