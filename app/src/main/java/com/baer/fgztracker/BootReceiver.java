package com.baer.fgztracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andy on 1/19/17
 */
public class BootReceiver extends BroadcastReceiver {

	private final Scheduler scheduler = new Scheduler();
	private final Notifier notifier = new Notifier();
	private final UserPrefs userPrefs = new UserPrefs();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (userPrefs.getEnabled(context)) {
			scheduler.rescheduleDaily(context);
			notifier.updateOngoingNotification(context, userPrefs.getTrackingResult(context));
		}
	}

}
