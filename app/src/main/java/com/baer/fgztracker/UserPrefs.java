package com.baer.fgztracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by TZHBAANM on 18.01.2017
 */

class UserPrefs {

	static boolean getEnabled(Context context) {
		return getPreferences(context).getBoolean("enabled", true);
	}

	static void setEnabled(Context context, boolean enabled) {
		getPreferences(context).edit().putBoolean("enabled", enabled).apply();
	}

	static String getTrackingUrl(Context context) {
		return getPreferences(context).getString("url", "https://fgzzh.emonitor.ch");
	}

	static String getSiteContent(Context context) {
		return getPreferences(context).getString("content", null);
	}

	static void setSiteContentAndResult(Context context, String content, String result) {
		getPreferences(context).edit().putString("content", content)
				.putString("result", result).apply();
	}

	static String getTrackingResult(Context context) {
		return getPreferences(context).getString("result", "no checks run yet");
	}

	static void setTrackingResult(Context context, String result) {
		getPreferences(context).edit().putString("result", result).apply();
	}

	static int getHour(Context context) {
		return getPreferences(context).getInt("hour", 15);
	}

	static int getMinute(Context context) {
		return getPreferences(context).getInt("minute", 55);
	}

	static int getInterval(Context context) {
		return getPreferences(context).getInt("interval", 1);
	}

	static int getRepeatCount(Context context) {
		return getPreferences(context).getInt("repeatCount", 30);
	}

	static void setHourMinuteIntervalRepeatCount(Context context, int hour, int minute,
												 int interval, int repeatCount) {
		getPreferences(context).edit()
				.putInt("hour", hour)
				.putInt("minute", minute)
				.putInt("interval", interval)
				.putInt("repeatCount", repeatCount)
				.apply();
	}

	private static SharedPreferences getPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
