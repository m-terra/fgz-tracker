package com.baer.fgztracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

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
	public static final String targetUrl = "https://www.timeanddate.com/worldclock/personal.html";
	//public static final String targetUrl = "https://fgzzh.emonitor.ch";
	public static final String viewUrl = "http://www.fgzzh.ch/index.cfm?Nav=22";
	public static final String CONTENT = "CONTENT";
	public static final String RESULT = "RESULT";
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
		} else if (checkCount > 17) {
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

		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + 2000 * 60, 2000 * 60, pendingIntent);
	}

	private void cancelRepeating() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		checkCount = 0;
		pendingIntent = null;
	}

	private void houseFoundAlert(Context context) {
		Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewUrl));
		Utils.createAlertNotification(context, "FGZ has changed, click to view", resultIntent);
	}

	private void runChecker() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return fetchPage();
			}

			@Override
			protected void onPostExecute(String currContent) {
				super.onPostExecute(currContent);
				compareContent(currContent);
				Utils.updateOngoingNotification(CheckerService.this);
			}
		}.execute();
	}


	private String fetchPage() {
		try {
			URL url = new URL(targetUrl);
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

	private void compareContent(String currContent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String prevContent = prefs.getString(CONTENT, null);
		String time = sdf.format(new Date());
		if (StringUtils.startsWith(currContent, FAILURE)) {
			prefs.edit().putString(RESULT, currContent).apply();
		} else if (StringUtils.isEmpty(prevContent)) {
			prefs.edit().putString(CONTENT, currContent)
					.putString(RESULT, "First Checked: " + time).apply();
		} else {
			if (StringUtils.equals(prevContent, currContent)) {
				prefs.edit().putString(CONTENT, currContent)
						.putString(RESULT, "No Change: " + time).apply();
			} else {
				prefs.edit().putString(CONTENT, currContent)
						.putString(RESULT, "Change detected: " + time).apply();
				houseFoundAlert(this);
				cancelRepeating();
			}
		}
	}

}
