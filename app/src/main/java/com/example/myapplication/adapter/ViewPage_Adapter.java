package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.R;

import java.util.List;

public class ViewPage_Adapter extends FragmentPagerAdapter {

    private List<Fragment> fragList;
    private List<String> tab_name;

    public ViewPage_Adapter(@NonNull FragmentManager fm, List<Fragment> fragList) {
        super(fm);
        this.fragList = fragList;
    }

    public ViewPage_Adapter(@NonNull FragmentManager fm, List<Fragment> fragList, List<String> tab_name) {
        super(fm);
        this.fragList = fragList;
        this.tab_name = tab_name;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (tab_name != null) {
            return tab_name.get(position);
        }else{
            return "";
        }
    }
}