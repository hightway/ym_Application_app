package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.R;

import java.util.List;

public class Mainpage_Adapter extends FragmentPagerAdapter {

    private List<Fragment> fragList;
    private Context context;
    private String[] str;
    private Integer[] ints;
    private Integer[] ints_tab;

    public Mainpage_Adapter(FragmentManager fm, List<Fragment> fragList, Context context, List<String> tab_name,
                            List<Integer> tab_icon_sel, List<Integer> tab_icon) {
        super(fm);
        this.fragList = fragList;
        this.context = context;
        this.str = tab_name.toArray(new String[tab_name.size()]);
        this.ints = tab_icon_sel.toArray(new Integer[tab_icon_sel.size()]);
        this.ints_tab = tab_icon.toArray(new Integer[tab_icon.size()]);
    }

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
        return str[position];
    }


    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.icon_view, null);
        ImageView iv = v.findViewById(R.id.tabicon);
        TextView tv = v.findViewById(R.id.tabtext);
        //iv.setBackgroundResource(ints[position]);
        if(position == 0){
            iv.setBackgroundResource(ints[position]);
        }else{
            iv.setBackgroundResource(ints_tab[position]);
        }
        tv.setText(str[position]);
        if (position == 0) {
            tv.setTextColor(v.getResources().getColor(R.color.red3));
        }
        return v;
    }

    /*public View getTabView_new(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.icon_view_new, null);
        ImageView iv = v.findViewById(R.id.tabicon);
        TextView tv = v.findViewById(R.id.tabtext);
        if(position != 1){
            iv.setBackgroundResource(ints[position]);
            tv.setText(str[position]);
            if (position == 0) {
                tv.setTextColor(v.getResources().getColor(R.color.red3));
            }
        }
        return v;
    }*/

}
