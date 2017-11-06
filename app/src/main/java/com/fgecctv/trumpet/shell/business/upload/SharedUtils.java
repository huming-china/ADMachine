package com.fgecctv.trumpet.shell.business.upload;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedUtils {
	
	//配置文件名称
	private static final String SHARED_NAME = "Advertisement";
	
	private static SharedPreferences getInstance(Context context, int mode,String shareName) {
		SharedPreferences sharedPreferences;
		if(shareName.isEmpty())
            sharedPreferences= context.getSharedPreferences(SHARED_NAME, mode); //读取文件,如果没有则会创建
		else
            sharedPreferences= context.getSharedPreferences(shareName, mode);
		return sharedPreferences;
	}

	private static Editor getShareEditor(Context context,String shareName) {
		SharedPreferences sharedPreferences = getInstance(context,
				Context.MODE_WORLD_WRITEABLE,shareName);
		return sharedPreferences.edit();
	}

	public static boolean putString(String shareName,Context context, String key, String value) {
		Editor editor = getShareEditor(context,shareName);
		editor.putString(key, value);
		return editor.commit();
	}

	public static String getString(String shareName,Context context, String key) {
		SharedPreferences sharedPreferences = getInstance(context,
				Context.MODE_WORLD_READABLE,shareName);
		return sharedPreferences.getString(key, "");
	}
}
