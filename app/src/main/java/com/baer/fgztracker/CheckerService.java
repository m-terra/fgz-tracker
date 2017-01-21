package com.baer.fgztracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckerService extends Service {

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM - HH:mm:ss", Locale.getDefault());
	private static final String FAILURE = "FAILURE ";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		runChecker();
		return Service.START_NOT_STICKY;
	}

	private void createChangeDetectedAlert(Context context) {
		Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UserPrefs.getTrackingUrl(context)));
		NotificationUtils.createAlertNotification(context, "FGZ has changed, click to view", resultIntent);
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
				String result = compareContent(newContent);
				NotificationUtils.updateOngoingNotification(CheckerService.this, result);
			}
		}.execute();
	}

	private String fetchPage() {
		BufferedReader inputReader = null;
		try {
			String url = UserPrefs.getTrackingUrl(this);
			Log.d("CheckerService", "Fetching content from url: " + url);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Cache-Control", "tmax-age=0");
			conn.connect();

			StringBuilder sb = new StringBuilder();
			inputReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = inputReader.readLine()) != null) {
				sb.append(inputLine);
			}
			conn.disconnect();
			return sb.toString();
		} catch (Exception e) {
			return FAILURE + sdf.format(new Date()) + ": " + e.getMessage();
		} finally {
			try {
				if (inputReader != null) {
					inputReader.close();
				}
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	private String compareContent(String newContent) {
		String result = newContent;
		String prevContent = UserPrefs.getSiteContent(this);
		String time = sdf.format(new Date());
		if (StringUtils.startsWith(newContent, FAILURE)) {
			UserPrefs.setTrackingResult(this, newContent);
		} else {
			if (StringUtils.isEmpty(prevContent)) {
				result = "first checked at " + time;
			} else if (StringUtils.equals(prevContent, newContent)) {
				result = "no change at " + time;
			} else {
				result = "change found at " + time;
				createChangeDetectedAlert(this);
			}
			UserPrefs.setSiteContentAndResult(this, newContent, result);
		}
		return result;
	}

}
