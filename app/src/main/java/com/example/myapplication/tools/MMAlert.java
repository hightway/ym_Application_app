package com.example.myapplication.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AlertAdapter;

import java.util.ArrayList;
import java.util.List;

public class MMAlert {

    public interface OnAlertSelectId {
        void onClick(int whichButton);
    }

    public interface OnAlertOkSelectId {
        void onOkClick(int whichButton,String content );
    }


    private MMAlert() {
    }


    /**
     * 拍照
     * 从相册中选择照片
     * @param context
     * @param title
     * @param items
     * @param exit
     * @param alertDo
     * @return
     */
    public static Dialog showAlert(final Context context, final String title, final String[] items,
                                   String exit, final OnAlertSelectId alertDo) {
        return showAlert(context, title, items, exit, alertDo, null);
    }



    /**
     * @param context
     *            Context.
     * @param title
     *            The title of this AlertDialog can be null .
     * @param items
     *            button name list.
     * @param alertDo
     *            methods call Id:Button + cancel_Button.
     * @param exit
     *            Name can be null.It will be Red Color
     * @return A AlertDialog
     */
    public static Dialog showAlert(final Context context, final String title, final String[] items, String exit,
                                   final OnAlertSelectId alertDo, DialogInterface.OnCancelListener cancelListener) {
        String cancel = context.getString(R.string.cancel);
        final Dialog dlg = new Dialog(context, R.style.MMThem_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.alert_dialog_menu_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        AlertAdapter adapter = new AlertAdapter(context, title, items, exit, cancel);
        list.setAdapter(adapter);
        list.setDividerHeight(0);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (!(title == null || title.equals("")) && position - 1 >= 0)
                {
                    alertDo.onClick(position - 1);
                    dlg.dismiss();
                    list.requestFocus();
                }
                else
                {
                    alertDo.onClick(position);
                    dlg.dismiss();
                    list.requestFocus();
                }

            }
        });
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null)
            dlg.setOnCancelListener(cancelListener);

        dlg.setContentView(layout);
        dlg.show();

        return dlg;
    }
}
