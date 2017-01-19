package com.baer.fgztracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

	private NumberPicker intervalPicker;
	private NumberPicker repeatCountPicker;
	private TimePicker startTimePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView urlView = (TextView) findViewById(R.id.url);
		urlView.setText(UserPrefs.getTrackingUrl(this));

		intervalPicker = (NumberPicker) findViewById(R.id.interval);
		intervalPicker.setMinValue(1);
		intervalPicker.setMaxValue(20);
		intervalPicker.setValue(UserPrefs.getInterval(this));

		repeatCountPicker = (NumberPicker) findViewById(R.id.repeatCount);
		repeatCountPicker.setMinValue(1);
		repeatCountPicker.setMaxValue(200);
		repeatCountPicker.setValue(UserPrefs.getRepeatCount(this));

		startTimePicker = (TimePicker) findViewById(R.id.startTime);
		startTimePicker.setIs24HourView(true);
		startTimePicker.setHour(UserPrefs.getHour(this));
		startTimePicker.setMinute(UserPrefs.getMinute(this));

		findViewById(R.id.save).setOnClickListener(this);

		SwitchCompat enableSwitch = (SwitchCompat) findViewById(R.id.enableSwitch);
		enableSwitch.setOnCheckedChangeListener(this);

		if (UserPrefs.getEnabled(this)) {
			Scheduler.scheduleDaily(this);
			enableSwitch.setChecked(true);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		UserPrefs.setEnabled(this, isChecked);
		if (isChecked) {
			Scheduler.scheduleDaily(this);
		} else {
			Scheduler.cancelDaily(this);
		}
	}

	@Override
	public void onClick(View v) {
		int hour = startTimePicker.getHour();
		int min = startTimePicker.getMinute();
		int interv = intervalPicker.getValue();
		int repCnt = repeatCountPicker.getValue();
		UserPrefs.setHourMinuteIntervalRepeatCount(this, hour, min, interv, repCnt);

		if (UserPrefs.getEnabled(this)) {
			Scheduler.scheduleDaily(this, hour, min);
		}
	}


}
