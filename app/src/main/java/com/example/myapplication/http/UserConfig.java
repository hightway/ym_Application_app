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
	public boolean isloaded = false;

	//oss数据
	public String AssumedRoleId;
	public String Bucket;
	public String OssRegion;
	public String AccessKeyId;
	public String AccessKeySecret;
	public String Expiration;
	public String SecurityToken;

	public String register_type;
	public String avatar;
	public String email;
	public String user_awaken_time;
	public String user_bedtime;
	public String user_sleep_monitoring;
	public String user_painless_arousal;
	public String user_timed_close;
	public String user_delay;
	public int age;

	public String wx_openId;
	public String wx_unionId;
	public String wx_nickname;
	public String wx_user_icon;


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
		this.user_awaken_time = share.getString(context.getString(R.string.ymapp_user_awaken_time), "");
		this.user_bedtime = share.getString(context.getString(R.string.ymapp_user_bedtime), "");
		this.user_sleep_monitoring = share.getString(context.getString(R.string.ymapp_user_sleep_monitoring), "");
		this.user_painless_arousal = share.getString(context.getString(R.string.ymapp_user_painless_arousal), "");
		this.user_timed_close = share.getString(context.getString(R.string.ymapp_user_timed_close), "");
		this.user_delay = share.getString(context.getString(R.string.ymapp_user_delay), "");
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
		editor.putString(context.getString(R.string.ymapp_user_awaken_time), user_awaken_time);
		editor.putString(context.getString(R.string.ymapp_user_bedtime), user_bedtime);
		editor.putString(context.getString(R.string.ymapp_user_sleep_monitoring), user_sleep_monitoring);
		editor.putString(context.getString(R.string.ymapp_user_painless_arousal), user_painless_arousal);
		editor.putString(context.getString(R.string.ymapp_user_timed_close), user_timed_close);
		editor.putString(context.getString(R.string.ymapp_user_delay), user_delay);

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
		this.user_awaken_time = "";
		this.user_bedtime = "";
		this.user_sleep_monitoring = "";
		this.user_painless_arousal = "";
		this.user_timed_close = "";
		this.user_delay = "";
		this.isloaded = false;

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
		editor.putString(context.getString(R.string.ymapp_user_awaken_time), user_awaken_time);
		editor.putString(context.getString(R.string.ymapp_user_bedtime), user_bedtime);
		editor.putString(context.getString(R.string.ymapp_user_sleep_monitoring), user_sleep_monitoring);
		editor.putString(context.getString(R.string.ymapp_user_painless_arousal), user_painless_arousal);
		editor.putString(context.getString(R.string.ymapp_user_timed_close), user_timed_close);
		editor.putString(context.getString(R.string.ymapp_user_delay), user_delay);

		editor.commit();
	}



	public void save_RawAudio_Sel(Context context, String word){
		if (context == null)
			return;
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = share.edit();

		editor.putString(context.getString(R.string.ymapp_userRaw_Audio), word);

		editor.commit();
	}

	public String get_RawAudio_Sel(Context context) {
		if (context == null)
			return "";
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(context);

		return share.getString(context.getString(R.string.ymapp_userRaw_Audio), "");
	}

}
