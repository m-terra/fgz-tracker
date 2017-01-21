package com.baer.fgztracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by andy on 1/19/17
 */
class Scheduler {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
	static final String RESCHEDULE_FLAG = "RESCHEDULE_FLAG";
	private final UserPrefs userPrefs = new UserPrefs();

	void rescheduleDaily(Context context) {
		rescheduleDaily(context, userPrefs.getHour(context), userPrefs.getMinute(context),
				userPrefs.getRepeatCount(context), userPrefs.getInterval(context));
	}

	void rescheduleDaily(Context context, int hour, int minute, int repreatCount, int interval) {
		cancelDaily(context);

		Calendar alarmTime = Calendar.getInstance();
		alarmTime.set(Calendar.HOUR_OF_DAY, hour);
		alarmTime.set(Calendar.MINUTE, minute);
		alarmTime.set(Calendar.SECOND, 0);
		if (Calendar.getInstance().after(alarmTime)) {
			alarmTime.add(Calendar.DATE, 1);
		}

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		List<Integer> alarmIds = new ArrayList<>(repreatCount);
		Intent intent = new Intent(context, CheckerService.class);

		for (int i = 0; i < repreatCount; i++) {
			int requestCode = 5000 + i;
			alarmIds.add(requestCode);
			if (i == repreatCount - 1) {
				intent.putExtra(RESCHEDULE_FLAG, true);
			}
			PendingIntent pi = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis() + i * interval * 60000, pi);
		}

		userPrefs.setAlarmIds(context, alarmIds);
		Log.d("Scheduler", "Scheduled daily check at " + sdf.format(alarmTime.getTime()));
	}

	void cancelDaily(Context context) {
		for (Integer id : userPrefs.getAlarmIds(context)) {
			PendingIntent pi = PendingIntent.getService(context, id,
					new Intent(context, CheckerService.class), PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.cancel(pi);
		}
	}

}
