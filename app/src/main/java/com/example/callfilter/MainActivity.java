package com.example.callfilter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

	private Intent endCallIntent;
	private Switch mSwIsFilterOn;
	private EditText mEtSleepTime;
	private SharedPreferences mPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPreference = PreferenceManager.getDefaultSharedPreferences(this);

		endCallIntent = new Intent(MainActivity.this, CallFilterService.class);
		mSwIsFilterOn = (Switch)findViewById(R.id.sw_is_filter_on);
		mEtSleepTime = (EditText)findViewById(R.id.et_sleep_time);

		int sleepTime = mPreference.getInt(Constants.PREFERENCE_KEY_SLEEP_TIME, 1);
		mEtSleepTime.setText(String.valueOf(sleepTime));
		mEtSleepTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					int sleepTime = Integer.parseInt(s.toString());
					if(sleepTime > 0){
						mPreference.edit().putInt(Constants.PREFERENCE_KEY_SLEEP_TIME, sleepTime).apply();
					}else{
						Toast.makeText(MainActivity.this, "格式不正确", Toast.LENGTH_SHORT).show();
					}
				}catch (Exception e){
					Toast.makeText(MainActivity.this, "格式不正确", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if(isWorked(CallFilterService.class.getName())){
			mSwIsFilterOn.setChecked(true);
		}else{
			mSwIsFilterOn.setChecked(false);

		}

		mSwIsFilterOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					startService(endCallIntent);
				}else{
					stopService(endCallIntent);
				}
			}
		});
	}


	private boolean isWorked(String className) {
		ActivityManager myManager = (ActivityManager)getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals(className)) {
				return true;
			}
		}
		return false;
	}

}
