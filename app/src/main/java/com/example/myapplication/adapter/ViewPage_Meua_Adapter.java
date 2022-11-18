package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPage_Meua_Adapter extends FragmentPagerAdapter {

    private List<Fragment> fragList;
    private List<String> tab_name;

    public ViewPage_Meua_Adapter(FragmentManager fm, List<Fragment> fragList, List<String> tab_name){
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
        return tab_name.get(position);
    }
}
