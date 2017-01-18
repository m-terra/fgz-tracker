package com.baer.fgztracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckerService extends Service {

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM - HH:mm:ss", Locale.getDefault());
	private static final String FAILURE = "FAILURE ";
	private PendingIntent pendingIntent;
	private int checkCount;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		runChecker();
		if (pendingIntent == null) {
			scheduleRepeating();
			runChecker();
		} else if (checkCount > UserPrefs.getRepeatCount(this)) {
			cancelRepeating();
		} else {
			runChecker();
			checkCount++;
		}
		return Service.START_NOT_STICKY;
	}

	private void scheduleRepeating() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent serviceIntent = new Intent(this, CheckerService.class);
		pendingIntent = PendingIntent.getService(this,
				44, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		int interval = UserPrefs.getInterval(this) * 60 * 1000;
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + interval, interval, pendingIntent);
	}

	private void cancelRepeating() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		checkCount = 0;
		pendingIntent = null;
	}

	private void houseFoundAlert(Context context) {
		Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UserPrefs.getTrackingUrl(context)));
		Utils.createAlertNotification(context, "FGZ has changed, click to view", resultIntent);
	}

	private void runChecker() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return fetchPage();
			}

			@Override
			protected void onPostExecute(String newContent) {
				super.onPostExecute(newContent);
				compareContent(newContent);
				Utils.updateOngoingNotification(CheckerService.this);
			}
		}.execute();
	}

	private String fetchPage() {
		try {
			URL url = new URL(UserPrefs.getTrackingUrl(this));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Cache-Control", "tmax-age=0");
			conn.connect();

			StringBuilder sb = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			Utils.closeQuietly(in);
			conn.disconnect();
			return sb.toString();
		} catch (Exception e) {
			return FAILURE + sdf.format(new Date()) + ": " + e.getMessage();
		}
	}

	private void compareContent(String newContent) {
		String prevContent = UserPrefs.getSiteContent(this);
		String time = sdf.format(new Date());
		if (StringUtils.startsWith(newContent, FAILURE)) {
			UserPrefs.setTrackingResult(this, newContent);
		} else if (StringUtils.isEmpty(prevContent)) {
			UserPrefs.setSiteContentAndResult(this, newContent, "First Checked: " + time);
		} else if (StringUtils.equals(prevContent, newContent)) {
			UserPrefs.setSiteContentAndResult(this, newContent, "No Change: " + time);
		} else {
			UserPrefs.setSiteContentAndResult(this, newContent, "Change detected: " + time);
			houseFoundAlert(this);
			cancelRepeating();
		}
	}

}
