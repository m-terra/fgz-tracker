package com.baer.fgztracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

	private SwitchCompat enableSwitch;
	private TextInputEditText interval;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		enableSwitch = (SwitchCompat) findViewById(R.id.enableSwitch);
		enableSwitch.setOnCheckedChangeListener(this);
		interval = (TextInputEditText) findViewById(R.id.interval);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (prefs.getBoolean("enabled", true)) {
			Utils.scheduleDaily(this);
			enableSwitch.setChecked(true);
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		prefs.edit().putBoolean("enabled", isChecked).apply();
		if (isChecked) {
			Utils.scheduleDaily(this);
		} else {
			Utils.cancelDaily(this);
		}
	}
}
