package com.baer.fgztracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.apache.commons.lang3.StringUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by andy on 1/17/17
 */
class Notifier {

	private String CHANNEL_ID = "fgz_channel";
	private static final int ONGOING_NOTIFICATION_ID = 257896;
	private final UserPrefs userPrefs = new UserPrefs();

	void updateOngoingNotification(Context context, String text) {
		Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		StringBuilder sb = new StringBuilder(text);
		sb.append("\ndaily ");
		sb.append(StringUtils.leftPad(Integer.toString(userPrefs.getHour(context)), 2, "0")).append(":");
		sb.append(StringUtils.leftPad(Integer.toString(userPrefs.getMinute(context)), 2, "0"));
		sb.append(" > runs ").append(userPrefs.getRepeatCount(context)).append("x");
		sb.append(" > interval ").append(userPrefs.getInterval(context)).append("min");
		bigTextStyle.bigText(sb);

		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 710,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_MIN);
			notificationManager.createNotificationChannel(channel);
		}

		Notification notification = new Notification.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_house)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(text)
				.setStyle(bigTextStyle)
				.setContentIntent(pendingIntent).build();

		notification.flags = Notification.FLAG_ONGOING_EVENT;

		notificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
	}

	void removeOngoingNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(ONGOING_NOTIFICATION_ID);
	}

	void createAlertNotification(Context context, String text, Intent intent) {
		Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		bigTextStyle.bigText(text);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 715,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_MIN);
			notificationManager.createNotificationChannel(channel);
		}

		Notification notification = new Notification.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_house)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(text)
				.setStyle(bigTextStyle)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent).build();

		notificationManager.notify(98745, notification);
	}


}
