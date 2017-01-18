package com.baer.fgztracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tzhbaanm on 13.11.2014
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (UserPrefs.getEnabled(context)) {
			Utils.scheduleDaily(context);
		}
	}

}
