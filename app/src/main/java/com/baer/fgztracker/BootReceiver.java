package com.baer.fgztracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tzhbaanm on 13.11.2014
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.getBoolean("enabled", true)) {
			Utils.scheduleDaily(context);
		}
	}

}
