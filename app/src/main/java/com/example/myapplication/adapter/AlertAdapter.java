package com.example.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AlertAdapter extends BaseAdapter {
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_EXIT = 2;
    public static final int TYPE_CANCEL = 3;
    private List<String> items = new ArrayList<String>();
    private int[] types;
    private boolean isTitle = false;
    //	private boolean isCancle = false;
    private Context context;
    private int changeLenth;


    public AlertAdapter(Context context, String title, String[] items, String exit, String cancel) {
        changeLenth = items.length;

        for (int i = 0; i < items.length; i++) {
            this.items.add(items[i]);
        }

        this.types = new int[this.items.size() + 3];
        this.context = context;
        if (title != null && !title.equals("")) {
            types[0] = TYPE_TITLE;
            this.isTitle = true;
            this.items.add(0, title);
        }

        if (exit != null && !exit.equals("")) {
            // this.isExit = true;
            types[this.items.size()] = TYPE_EXIT;
            this.items.add(exit);
        }

        if (cancel != null && !cancel.equals("")) {
            // this.isSpecial = true;
            types[this.items.size()] = TYPE_CANCEL;
//			isCancle = true;
            this.items.add(cancel);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0 && isTitle) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String textString = (String) getItem(position);
        ViewHolder holder;
        int type = types[position];
        if (convertView == null || ((ViewHolder) convertView.getTag()).type != type) {
            holder = new ViewHolder();
            if (type == TYPE_CANCEL) {//
                convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_cancel, null);
                holder.text = (TextView) convertView.findViewById(R.id.popup_text);
            } else if (type == TYPE_BUTTON) {//
                convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout, null);
                holder.text = (TextView) convertView.findViewById(R.id.popup_text);
                if (changeLenth == 1) {
                    holder.text.setBackgroundResource(R.mipmap.white_btn_n);
                } else if (changeLenth >= 2) {
                    if (position == 0) {
                        holder.text.setBackgroundResource(R.mipmap.pop_top);
                    } else if (position == changeLenth - 1) {
                        holder.text.setBackgroundResource(R.mipmap.pop_bottom);
                    } else {
                        holder.text.setBackgroundResource(R.mipmap.pop_center);
                    }
                }
            } else if (type == TYPE_TITLE || type == TYPE_EXIT) {//
                convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_title, null);
                holder.text = (TextView) convertView.findViewById(R.id.popup_text);
            } /*else if (type == TYPE_EXIT) {
				convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_special, null);
			}*/
            holder.type = type;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(textString);
        return convertView;
    }

    static class ViewHolder {
        TextView text;
        int type;
    }
}