package com.example.myapplication.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.myapplication.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Keyboard_ABC_PopupWindow extends PopupWindow {
    private static final String TAG = "KeyboardPopupWindow";
    private Context context;
    private View anchorView;
    private View parentView;
    private EditText editText;
    //private boolean is_Key_Input = false;
    private boolean isRandomSort = false;
    private boolean is_ABC = false;
    private List<Integer> list = new ArrayList<>();
    private int[] commonButtonIds = new int[]{R.id.button_q, R.id.button_w, R.id.button_e, R.id.button_r,
            R.id.button_t, R.id.button_y, R.id.button_u, R.id.button_i, R.id.button_o, R.id.button_p,
            R.id.button_a, R.id.button_s, R.id.button_d, R.id.button_f, R.id.button_g, R.id.button_h, R.id.button_j, R.id.button_k,
            R.id.button_l, R.id.button_z, R.id.button_x, R.id.button_c, R.id.button_v, R.id.button_b, R.id.button_n, R.id.button_m};

    private int[] num_ButtonIds = new int[]{R.id.but_0, R.id.but_1, R.id.but_2, R.id.but_3,
            R.id.but_4, R.id.but_5, R.id.but_6, R.id.but_7, R.id.but_8, R.id.but_9};

    private String[] number = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private String[] ABC = new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M"};
    private String[] abc = new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m"};
    private Button button_abc;

    /**
     * @param context
     * @param anchorView
     * @param editText
     * @param isRandomSort 数字是否随机排序
     */
    public Keyboard_ABC_PopupWindow(Context context, View anchorView, EditText editText, boolean isRandomSort) {
        this.context = context;
        this.anchorView = anchorView;
        this.editText = editText;
        this.isRandomSort = isRandomSort;
        if (context == null || anchorView == null) {
            return;
        }
        initConfig();
        initView();
    }


    private void initConfig() {
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forbidDefaultSoftKeyboard();
    }

    /**
     * 禁止系统默认的软键盘弹出
     */
    private void forbidDefaultSoftKeyboard() {
        if (editText == null) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0以上，使用反射的方式禁止系统自带的软键盘弹出
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新自定义的popupwindow是否outside可触摸反应：如果是不可触摸的，则显示该软键盘view
     *
     * @param isTouchable
     */
    public void refreshKeyboardOutSideTouchable(boolean isTouchable) {
        setOutsideTouchable(isTouchable);
        if (!isTouchable) {
            Log.d(TAG, "执行show");
            show();
        } else {
            Log.d(TAG, "执行dismiss");
            dismiss();
        }
    }

    private void initView() {
        parentView = LayoutInflater.from(context).inflate(R.layout.key_board_word, null);
        initKeyboardView(parentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(parentView);
    }

    private void initKeyboardView(View view) {
        LinearLayout dropdownLl = view.findViewById(R.id.dropdownLl);
        dropdownLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //①给数字键设置点击监听
        for (int i = 0; i < commonButtonIds.length; i++) {
            final Button button = view.findViewById(commonButtonIds[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curSelection = editText.getSelectionStart();
                    int length = editText.getText().toString().length();
                    if (curSelection < length) {
                        String content = editText.getText().toString();
                        editText.setText(content.substring(0, curSelection) + button.getText() + content.subSequence(curSelection, length));
                        editText.setSelection(curSelection + 1);
                    } else {
                        editText.setText(editText.getText().toString() + button.getText());
                        editText.setSelection(editText.getText().toString().length());
                    }
                }
            });
        }

        for (int i = 0; i < num_ButtonIds.length; i++) {
            final Button button = view.findViewById(num_ButtonIds[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curSelection = editText.getSelectionStart();
                    int length = editText.getText().toString().length();
                    if (curSelection < length) {
                        String content = editText.getText().toString();
                        editText.setText(content.substring(0, curSelection) + button.getText() + content.subSequence(curSelection, length));
                        editText.setSelection(curSelection + 1);
                    } else {
                        editText.setText(editText.getText().toString() + button.getText());
                        editText.setSelection(editText.getText().toString().length());
                    }
                }
            });
        }

        //②给小数点按键设置点击监听
        /*view.findViewById(R.id.buttonDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curSelection = editText.getSelectionStart();
                int length = editText.getText().toString().length();
                if (curSelection < length) {
                    String content = editText.getText().toString();
                    editText.setText(content.substring(0, curSelection) + "." + content.subSequence(curSelection, length));
                    editText.setSelection(curSelection + 1);
                } else {
                    editText.setText(editText.getText().toString() + ".");
                    editText.setSelection(editText.getText().toString().length());
                }
            }
        });*/

        //③给叉按键设置点击监听
        view.findViewById(R.id.buttonCross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = editText.getText().toString().length();
                int curSelection = editText.getSelectionStart();
                if (length > 0 && curSelection > 0 && curSelection <= length) {
                    String content = editText.getText().toString();
                    editText.setText(content.substring(0, curSelection - 1) + content.subSequence(curSelection, length));
                    editText.setSelection(curSelection - 1);
                }
            }
        });

        view.findViewById(R.id.buttonCross).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText.setText("");
                return true;
            }
        });

        /*view.findViewById(R.id.button_num).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyboard_callback != null){
                    keyboard_callback.change_num();
                }
            }
        });*/

        //大小写切换
        button_abc = view.findViewById(R.id.button_ABC);
        button_abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_ABC();
            }
        });
    }


    /*public void setIs_Key_Input(boolean key) {
        this.is_Key_Input = key;
    }*/


    public void show() {
        if (!isShowing() && anchorView != null) {
            doRandomSortOp();
            this.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 随机分布数字
     */
    private void doRandomSortOp() {
        if (parentView == null) {
            return;
        }
        if (!isRandomSort) {
            is_ABC = false;
            for (int i = 0; i < commonButtonIds.length; i++) {
                final Button button = parentView.findViewById(commonButtonIds[i]);
                button.setText(abc[i]);
            }

            /*for (int i = 0; i < num_ButtonIds.length; i++) {
                final Button button = parentView.findViewById(commonButtonIds[i]);
                button.setText(number[i]);
            }*/
            button_abc.setText("大写");
        } else {
            list.clear();
            Random ran = new Random();
            while (list.size() < commonButtonIds.length) {
                int n = ran.nextInt(commonButtonIds.length);
                if (!list.contains(n))
                    list.add(n);//如果n不包涵在list中，才添加
            }
            for (int i = 0; i < commonButtonIds.length; i++) {
                final Button button = parentView.findViewById(commonButtonIds[i]);
                button.setText("" + list.get(i));
            }
        }

        setAnimationStyle(R.style.keyboard);
    }


    private void set_ABC() {
        if (parentView == null) {
            return;
        }
        if (is_ABC) {
            for (int i = 0; i < commonButtonIds.length; i++) {
                final Button button = parentView.findViewById(commonButtonIds[i]);
                button.setText(abc[i]);
            }
            button_abc.setText("大写");
        }else{
            for (int i = 0; i < commonButtonIds.length; i++) {
                final Button button = parentView.findViewById(commonButtonIds[i]);
                button.setText(ABC[i]);
            }
            button_abc.setText("小写");
        }
        is_ABC = !is_ABC;
    }


    public void refreshViewAndShow(Context context, View anchorView, EditText editText) {
        this.context = context;
        this.anchorView = anchorView;
        this.editText = editText;
        if (context == null || anchorView == null) {
            return;
        }
        show();
    }

    public boolean isRandomSort() {
        return isRandomSort;
    }

    public void setRandomSort(boolean randomSort) {
        isRandomSort = randomSort;
    }

    public void releaseResources() {
        this.dismiss();
        context = null;
        anchorView = null;
        if (list != null) {
            list.clear();
            list = null;
        }
    }

    /*private Keyboard_NUM_Callback keyboard_callback;
    public interface Keyboard_NUM_Callback {
        void change_num();
    }
    public void setKeyboard_NUM_Callback(Keyboard_NUM_Callback keyboard_callback) {
        this.keyboard_callback = keyboard_callback;
    }*/


}
