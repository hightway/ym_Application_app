package com.example.myapplication.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class VerificationCodeViewJava extends RelativeLayout implements KeyboardPopupWindow.Keyboard_Callback{

    public EditText editText;
    private List<TextView> textViewList = new ArrayList<>();
    private StringBuffer stringBuffer = new StringBuffer();
    private KeyboardPopupWindow keyboardPopupWindow;

    public VerificationCodeViewJava(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationCodeViewJava(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //添加布局内容
        View.inflate(context, R.layout.view_verification_code, this);
        editText = findViewById(R.id.editCode);
        //光标不可见
        editText.setCursorVisible(false);
        textViewList.add(findViewById(R.id.txtCode1));
        textViewList.add(findViewById(R.id.txtCode2));
        textViewList.add(findViewById(R.id.txtCode3));
        textViewList.add(findViewById(R.id.txtCode4));
        textViewList.add(findViewById(R.id.txtCode5));

        /*InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);*/

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果有字符输入时才进行操作
                if (!s.toString().equals("")) {
                    //我们限制了5个验证码
                    if (stringBuffer.length() > 4) {
                        editText.setText("");
                        return;
                    } else {
                        stringBuffer.append(s);
                        //因为editText是辅助的，根本字符串是stringBuffer，所以将EditText置空
                        editText.setText("");
                        //现在很多App都是输入完毕后自动进入下一步逻辑，所以咱们一般都是在这监听，完成后进行回调业务即可
                        if (stringBuffer.length() == 5) {
                            //验证码输入完毕了，自动进行验证逻辑
                            if(key_callBack != null){
                                key_callBack.key_back(stringBuffer.toString(), editText);
                            }
                        }
                    }

                    for (int i = 0; i < stringBuffer.length(); i++) {
                        textViewList.get(i).setText(stringBuffer.charAt(i) + "");
                    }
                }
            }
        });

        //设置删除按键的监听
        /*editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (stringBuffer.length() > 0) {
                        //删除字符
                        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
                        //将TextView显示内容置空
                        textViewList.get(stringBuffer.length()).setText("");
                    }
                    return true;
                }
                return false;
            }
        });*/
    }


    public Key_CallBack key_callBack;

    @Override
    public void txt_del() {
        if (stringBuffer.length() > 0) {
            //删除字符
            stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
            //将TextView显示内容置空
            textViewList.get(stringBuffer.length()).setText("");
        }
    }

    public void setkeyboardPopupWindow(KeyboardPopupWindow keyboard){
        this.keyboardPopupWindow = keyboard;
        this.keyboardPopupWindow.setKeyboard_Callback(this);
    }

    public interface Key_CallBack{
        void key_back(String txt, EditText editText);
    }
    public void setKey_CallBack(Key_CallBack callBack){
        this.key_callBack = callBack;
    }

}
