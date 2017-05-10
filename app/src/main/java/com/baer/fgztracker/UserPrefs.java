package com.baer.fgztracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by andy on 1/19/17
 */
class UserPrefs {

	boolean getEnabled(Context context) {
		return getPreferences(context).getBoolean("enabled", true);
	}

	void setEnabled(Context context, boolean enabled) {
		getPreferences(context).edit().putBoolean("enabled", enabled).apply();
	}

	String getTrackingUrl(Context context) {
		return getPreferences(context).getString("url", "https://fgzzh.emonitor.ch");
	}

	String getSiteContent(Context context) {
		return getPreferences(context).getString("content", null);
	}

	void setSiteContentAndResult(Context context, String content, String result) {
		getPreferences(context).edit().putString("content", content)
				.putString("result", result).apply();
	}

	String getTrackingResult(Context context) {
		String result = getPreferences(context).getString("result", null);
		if (result == null) {
			StringBuilder sb = new StringBuilder("first check will run at ");
			sb.append(StringUtils.leftPad(Integer.toString(getHour(context)), 2, "0")).append(":");
			sb.append(StringUtils.leftPad(Integer.toString(getMinute(context)), 2, "0"));
			result = sb.toString();
		}
		return result;
	}

	void setTrackingResult(Context context, String result) {
		getPreferences(context).edit().putString("result", result).apply();
	}

	int getHour(Context context) {
		return getPreferences(context).getInt("hour", 15);
	}

	int getMinute(Context context) {
		return getPreferences(context).getInt("minute", 55);
	}

	int getInterval(Context context) {
		return getPreferences(context).getInt("interval", 2);
	}

	int getRepeatCount(Context context) {
		return getPreferences(context).getInt("repeatCount", 15);
	}

	void setHourMinuteRepeatCountInterval(Context context, int hour, int minute,
										  int repeatCount, int interval) {
		getPreferences(context).edit()
				.putInt("hour", hour)
				.putInt("minute", minute)
				.putInt("repeatCount", repeatCount)
				.putInt("interval", interval)
				.apply();
	}

	void setAlarmIds(Context context, List<Integer> alarmIds) {
		Set<String> stringIds = new HashSet<>(alarmIds.size());
		for (Integer alarmId : alarmIds) {
			stringIds.add(String.valueOf(alarmId));
		}
		getPreferences(context).edit().putStringSet("alarmIds", stringIds).apply();
	}

	List<Integer> getAlarmIds(Context context) {
		List<Integer> alarmIds = new ArrayList<>();
		Set<String> set = getPreferences(context).getStringSet("alarmIds", null);
		if (set != null) {
			for (String id : set) {
				alarmIds.add(Integer.parseInt(id));
			}
		}
		return alarmIds;
	}

	private SharedPreferences getPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
