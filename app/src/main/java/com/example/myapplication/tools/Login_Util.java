package com.example.myapplication.tools;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication.activity.Password_Login_Activity;
import com.example.myapplication.activity.WelcomeActivity;
import com.example.myapplication.activity.WelcomePage_Activity;

public class Login_Util {

    public static void go_Login(Context context){
        context.startActivity(new Intent(context, WelcomeActivity.class));
    }

}
