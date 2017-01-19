package com.baer.fgztracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by andy on 1/17/17
 */

class NotificationUtils {

	private static final int ONGOING_NOTIFICATION_ID = 257896;

	static void updateOngoingNotification(Context context, String text) {
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		StringBuilder sb = new StringBuilder(text);
		sb.append("\ndaily ");
		sb.append(StringUtils.leftPad(Integer.toString(UserPrefs.getHour(context)), 2, "0")).append(":");
		sb.append(StringUtils.leftPad(Integer.toString(UserPrefs.getMinute(context)), 2, "0"));
		sb.append(" > repeat ").append(UserPrefs.getRepeatCount(context)).append("x");
		sb.append(" > interval ").append(UserPrefs.getInterval(context)).append("min");
		bigTextStyle.bigText(sb);

		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 710,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);


		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_house)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(text)
				.setStyle(bigTextStyle)
				.setPriority(Notification.PRIORITY_MIN)
				.setContentIntent(pendingIntent).build();

		notification.flags = Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
	}

	static void removeOngoingNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(ONGOING_NOTIFICATION_ID);
	}

	static void createAlertNotification(Context context, String text, Intent intent) {
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		bigTextStyle.bigText(text);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 715,
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

		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(98745, notification);
	}



}
