package com.example.myapplication.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.myapplication.R;


/**
 * Created by zhao on 2019/6/14.
 */
public class LoadingDialog extends Dialog {

    private String txt;

    public LoadingDialog(Context context, String txt) {
        super(context, R.style.myDialogTheme2);
        this.txt = txt;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commom_loading_layout);
        TextView tv_load = findViewById(R.id.tv_load);
        if(!TextUtils.isEmpty(txt)){
            tv_load.setText(txt);
        }
    }

}
