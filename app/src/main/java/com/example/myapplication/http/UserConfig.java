package com.example.myapplication.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.example.myapplication.R;

public class UserConfig {
	
	public int user_id;
	public String name;
	public String phone;
	public String access_token;
	public String token_type;
	public int expires_in;

	public String register_type;
	public String avatar;
	public String email;
	public int age;


	private static UserConfig instance;
	private UserConfig(){
		
	}
	public static synchronized UserConfig instance(){
		if(instance == null){
			instance = new UserConfig();
		}
		return instance;
	}

	
	public void getUserConfig(Context context) {
		if (context == null)
			return;
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(context);

		this.user_id = share.getInt(context.getString(R.string.ymapp_userId), 0);
		this.age = share.getInt(context.getString(R.string.ymapp_userAge), 0);
		this.expires_in = share.getInt(context.getString(R.string.ymapp_userExpires_in), 0);
		this.name = share.getString(context.getString(R.string.ymapp_userName), "");
		this.phone = share.getString(context.getString(R.string.ymapp_userPhone), "");
		this.access_token = share.getString(context.getString(R.string.ymapp_userAccess_token), "");
		this.token_type = share.getString(context.getString(R.string.ymapp_userToken_type), "");
		this.register_type = share.getString(context.getString(R.string.ymapp_userRegister_type), "");
		this.avatar = share.getString(context.getString(R.string.ymapp_userAvatar), "");
		this.email = share.getString(context.getString(R.string.ymapp_userEmail), "");
	}
	
	public void saveUserConfig(Context context){
		if (context == null)
			return;
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = share.edit();
		
		editor.putInt(context.getString(R.string.ymapp_userId), user_id);
		editor.putInt(context.getString(R.string.ymapp_userExpires_in), expires_in);
		editor.putInt(context.getString(R.string.ymapp_userAge), age);
		editor.putString(context.getString(R.string.ymapp_userName), name);
		editor.putString(context.getString(R.string.ymapp_userPhone), phone);
		editor.putString(context.getString(R.string.ymapp_userAccess_token), access_token);
		editor.putString(context.getString(R.string.ymapp_userToken_type), token_type);
		editor.putString(context.getString(R.string.ymapp_userRegister_type), register_type);
		editor.putString(context.getString(R.string.ymapp_userAvatar), avatar);
		editor.putString(context.getString(R.string.ymapp_userEmail), email);

		editor.commit();
	}
	
	public void exit(Context context){
		if (context == null)
			return;
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = share.edit();
		
		this.user_id = 0;
		this.expires_in = 0;
		this.age = 0;
		this.name = "";
		this.phone = "";
		this.access_token = "";
		this.token_type = "";
		this.register_type = "";
		this.avatar = "";
		this.email = "";

		editor.putInt(context.getString(R.string.ymapp_userId), user_id);
		editor.putInt(context.getString(R.string.ymapp_userExpires_in), expires_in);
		editor.putInt(context.getString(R.string.ymapp_userAge), age);
		editor.putString(context.getString(R.string.ymapp_userName), name);
		editor.putString(context.getString(R.string.ymapp_userPhone), phone);
		editor.putString(context.getString(R.string.ymapp_userAccess_token), access_token);
		editor.putString(context.getString(R.string.ymapp_userToken_type), token_type);
		editor.putString(context.getString(R.string.ymapp_userRegister_type), register_type);
		editor.putString(context.getString(R.string.ymapp_userAvatar), avatar);
		editor.putString(context.getString(R.string.ymapp_userEmail), email);

		editor.commit();
	}
}
