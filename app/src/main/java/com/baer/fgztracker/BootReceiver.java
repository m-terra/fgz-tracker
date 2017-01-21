package com.baer.fgztracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andy on 1/19/17
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (UserPrefs.getEnabled(context)) {
			Scheduler.rescheduleDaily(context);
			Notifier.updateOngoingNotification(context, UserPrefs.getTrackingResult(context));
		}
	}

}
