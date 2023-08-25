package qnopy.com.qnopyandroid.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
	public static Context globalContext = null;
	final static String file = "RETRACE";
	final static String firstTime = "FirstTime";
	
	public static String getString (String key, String def) {
		
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getString(key, def);
	}
	public static int getInt (String key, int def) {
		
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getInt(key, def);
	}
	public static Boolean getBoolean (String key, Boolean def) {
		
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getBoolean(key, def);
	}
	public static void putString (String key, String value) {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}
	public static void putInt (String key, int value) {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.commit();
	}
	public static void putBoolean (String key, Boolean value) {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static Boolean getRetracing () {
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getBoolean("RETRACE", false);
	}
	public static void resetRetracing () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean("RETRACE", false);
		editor.commit();
	}
	
	public static Boolean getCamOrMap() {
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getBoolean("CAMORMAP", false);
	}
	public static void setCamOrMap () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean("CAMORMAP", true);
		editor.commit();
	}
	public static void resetCamOrMap () {
		try {
			SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
			editor.putBoolean("CAMORMAP", false);
			editor.commit();
		}catch (NullPointerException n){
			n.printStackTrace();
		}
	}
	public static Boolean isFirstTime () {
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getBoolean(firstTime, true);
	}
	public static void setFirstTimeFalse () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean(firstTime, false);
		editor.commit();
	}
	
	public static Boolean isSaved() {
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getBoolean("saved", true);
	}
	public static void setSaved () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean("saved", true);
		editor.commit();
	}
	public static void resetSaved () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean("saved", false);
		editor.commit();
	}
	public static void enableDownload () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean("download", true);
		editor.commit();
	}
	public static void disableDownload () {
		SharedPreferences.Editor editor = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE).edit();
		editor.putBoolean("download", false);
		editor.commit();
	}
	public static Boolean isDownloadEnabled() {
		SharedPreferences shared = globalContext.getSharedPreferences(file, globalContext.MODE_PRIVATE);
		return shared.getBoolean("download", true);
	}
}