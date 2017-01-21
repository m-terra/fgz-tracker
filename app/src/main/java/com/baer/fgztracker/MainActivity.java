package com.baer.fgztracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by andy on 1/19/17
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

	private final Scheduler scheduler = new Scheduler();
	private final Notifier notifier = new Notifier();
	private final UserPrefs userPrefs = new UserPrefs();
	private NumberPicker intervalPicker;
	private NumberPicker repeatCountPicker;
	private TimePicker startTimePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
		setSupportActionBar(appbar);

		TextView urlView = (TextView) findViewById(R.id.url);
		urlView.setText(userPrefs.getTrackingUrl(this));

		intervalPicker = (NumberPicker) findViewById(R.id.interval);
		intervalPicker.setMinValue(1);
		intervalPicker.setMaxValue(20);
		intervalPicker.setValue(userPrefs.getInterval(this));

		repeatCountPicker = (NumberPicker) findViewById(R.id.repeatCount);
		repeatCountPicker.setMinValue(1);
		repeatCountPicker.setMaxValue(200);
		repeatCountPicker.setValue(userPrefs.getRepeatCount(this));

		startTimePicker = (TimePicker) findViewById(R.id.startTime);
		startTimePicker.setIs24HourView(true);
		startTimePicker.setHour(userPrefs.getHour(this));
		startTimePicker.setMinute(userPrefs.getMinute(this));

		findViewById(R.id.save).setOnClickListener(this);

		SwitchCompat enableSwitch = (SwitchCompat) findViewById(R.id.enableSwitch);
		enableSwitch.setOnCheckedChangeListener(this);

		if (userPrefs.getEnabled(this)) {
			scheduler.rescheduleDaily(this);
			notifier.updateOngoingNotification(this, userPrefs.getTrackingResult(this));
			enableSwitch.setChecked(true);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		userPrefs.setEnabled(this, isChecked);
		if (isChecked) {
			scheduler.rescheduleDaily(this);
			notifier.updateOngoingNotification(this, userPrefs.getTrackingResult(this));
		} else {
			scheduler.cancelDaily(this);
			notifier.removeOngoingNotification(this);
		}
	}

	@Override
	public void onClick(View v) {
		int hour = startTimePicker.getHour();
		int min = startTimePicker.getMinute();
		int repreatCount = repeatCountPicker.getValue();
		int interval = intervalPicker.getValue();
		userPrefs.setHourMinuteRepeatCountInterval(this, hour, min, repreatCount, interval);

		if (userPrefs.getEnabled(this)) {
			scheduler.rescheduleDaily(this, hour, min, repreatCount, interval);
			notifier.updateOngoingNotification(this, userPrefs.getTrackingResult(this));
		}
	}

}
