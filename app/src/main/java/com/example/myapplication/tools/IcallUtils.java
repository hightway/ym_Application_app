package com.example.myapplication.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcallUtils {

	public static boolean isNull(String s) {
		return s == null || s.equals("") || s.equals("null");
	}

	public static boolean isPwd(String pwd) {

		Pattern p = Pattern.compile("[a-zA-Z0-9]*");
		Matcher m = p.matcher(pwd);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 实现粘贴功能 add by wangqianzhou
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String paste(Context context) {
		try {
			// 得到剪贴板管理器
			ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			return cmb.getText().toString().trim();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	// 手机号校验
	public static boolean isPhone(String number) {

		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(number);
		if (m.matches()) {
			if (number.length() == 11 && !number.startsWith("0")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取屏幕的宽度
	 * 
	 * @return
	 */
	public static int[] getpx(Activity act) {
		int[] px = new int[2];
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels;
		px[0] = height;
		px[1] = width;
		return px;
	}

	/**
	 *  生成随即验证码
	 * @return
	 */
	public static String getVCodeStr() {
		Random r = new Random();
		Double d = r.nextDouble();
		String str = d + "";
		str = str.substring(3, 3 + 6);
		return str;
	}
	
	
	/**
	 * 格斯转化为两位小数点的字符串
	 * @param str
	 * @return
	 */
	public static String getTuoPointStr(String str){
		float b = Float.parseFloat(str);
		DecimalFormat d = new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = d.format(b);//format 返回的是字符串
		return p;
	}
	
	
	public static void hideSoftInput(Activity act){
		try {
			((InputMethodManager) act.getSystemService(act.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	public static void downloadmp3(String mp3url){
		String urlDownload = "";
		urlDownload = mp3url;
		// 获得存储卡路径，构成 保存文件的目标路径
		String dirName = "";
		dirName = Environment.getExternalStorageDirectory()+"/data/";
		File f = new File(dirName);
		if(!f.exists())
		{
		    f.mkdir();
		}
		String newFilename = urlDownload.substring(urlDownload.lastIndexOf("/")+1);
		newFilename =dirName + newFilename;
		File file = new File(newFilename);
		//如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if(file.exists())
		{
		    file.delete();
		}
		try {
			URL url = new URL(urlDownload);
			// 打开连接   
			URLConnection con = url.openConnection();
			//获得文件的长度
			int contentLength = con.getContentLength();
			// 输入流   
			InputStream is = con.getInputStream();
			// 1K的数据缓冲   
			byte[] bs = new byte[1024];   
			// 读取到的数据长度   
			int len;   
			// 输出的文件流   
			OutputStream os = new FileOutputStream(newFilename);
			// 开始读取   
			while ((len = is.read(bs)) != -1) {   
			    os.write(bs, 0, len);   
			}
			// 完毕，关闭所有链接   
			os.close();  
			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;  
        int minute = 0;  
        int second = 0;  
        if (time <= 0)  
            return "00:00:00";  
        else {  
            minute = time / 60;  
            if (minute < 60) {  
                second = time % 60;  
                timeStr = unitFormat(hour) + ":"+ unitFormat(minute) + ":" + unitFormat(second);  
            } else {  
                hour = minute / 60;  
                if (hour > 99)  
                    return "99:59:59";  
                minute = minute % 60;  
                second = time - hour * 3600 - minute * 60;  
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);  
            }  
        }  
        return timeStr;  
    }  
  
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)  
            retStr = "0" + Integer.toString(i);
        else  
            retStr = "" + i;  
        return retStr;  
    }
}
