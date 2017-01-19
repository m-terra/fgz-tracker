package com.baer.fgztracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by andy on 1/19/17
 */

class Scheduler {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

	static void scheduleDaily(Context context) {
		scheduleDaily(context, UserPrefs.getHour(context), UserPrefs.getMinute(context));
	}

	static void scheduleDaily(Context context, int hour, int minute) {
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.set(Calendar.HOUR_OF_DAY, hour);
		alarmTime.set(Calendar.MINUTE, minute);
		alarmTime.set(Calendar.SECOND, 0);
		if (Calendar.getInstance().after(alarmTime)) {
			alarmTime.add(Calendar.DATE, 1);
		}
		PendingIntent pi = PendingIntent.getService(context, 22,
				new Intent(context, CheckerService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pi);

		NotificationUtils.updateOngoingNotification(context, UserPrefs.getTrackingResult(context));

		Log.d("CheckerService", "Scheduled daily check at " + sdf.format(alarmTime.getTime()));
	}

	static void cancelDaily(Context context) {
		PendingIntent pi = PendingIntent.getService(context, 22,
				new Intent(context, CheckerService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);

		NotificationUtils.removeOngoingNotification(context);
	}
}
