package com.example.myapplication.tools;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

public class DialogUtils {

    private static Dialog dialog;
    private static DialogUtils du;

    public DialogUtils() {

    }

    public static DialogUtils getInstance() {

        if (du == null) {
            du = new DialogUtils();
        }
        return du;
    }

    public void showDialog(Context context, String content) {
        int width = ScreenSize.screenWidth(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width / 3, width / 3);
        LayoutInflater inflater = LayoutInflater.from(context);
        dialog = new Dialog(context, R.style.MyDialogStyle);
        View v = inflater.inflate(R.layout.dialog, null);
        TextView tv = (TextView) v.findViewById(R.id.textViewContent);
        tv.setText(content);
        dialog.setContentView(v, params);
        dialog.show();
        System.out.println("-----进入执行----showDialog");
    }

    public  boolean isShow(){
        System.out.println("-----进入----isShow");
        if (dialog!=null){
            return dialog.isShowing();
        }else {
            return false;
        }
    }

    public void dismiss() {
        System.out.println("-----进入----dismiss()");
        if (dialog != null) {
            dialog.dismiss();
            System.out.println("---执行------dismiss");
        }
    }


}
