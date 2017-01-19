package com.baer.fgztracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by andy on 1/17/17
 */

class Utils {

	private static final int ONGOING_NOTIFICATION_ID = 257896;

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

		updateOngoingNotification(context);

		Log.d("CheckerService", "Checking schedules for " + alarmTime);
	}

	static void cancelDaily(Context context) {
		PendingIntent pi = PendingIntent.getService(context, 22,
				new Intent(context, CheckerService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);

		removeOngoingNotification(context);
	}

	static void updateOngoingNotification(Context context) {
		String text = UserPrefs.getTrackingResult(context);
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		StringBuilder sb = new StringBuilder(text);
		sb.append("\n- Checks start daily at ");
		sb.append(UserPrefs.getHour(context)).append(":");
		sb.append(UserPrefs.getMinute(context));
		sb.append("\n- Checking interval is ").append(UserPrefs.getInterval(context)).append(" min");
		sb.append("\n- Check is repeated ").append(UserPrefs.getRepeatCount(context)).append(" times");
		sb.append("\n- Traget url is ").append(UserPrefs.getTrackingUrl(context));
		bigTextStyle.bigText(sb);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_house)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(text)
				.setStyle(bigTextStyle)
				.setPriority(Notification.PRIORITY_MIN).build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
	}

	private static void removeOngoingNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(ONGOING_NOTIFICATION_ID);
	}

	static void createAlertNotification(Context context, String text, Intent intent) {
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		bigTextStyle.bigText(text);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_house)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(text)
				.setStyle(bigTextStyle)
				.setAutoCancel(true)
				.setPriority(Notification.PRIORITY_MAX)
				.setContentIntent(pendingIntent).build();

		notification.defaults = Notification.DEFAULT_ALL;
		mNotifyMgr.notify(98745, notification);
	}

	static void closeQuietly(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

}
