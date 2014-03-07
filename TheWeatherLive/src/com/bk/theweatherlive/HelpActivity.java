package com.bk.theweatherlive;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class HelpActivity extends Activity {

	TextView settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		displaySettings();
	}
	
	public void displaySettings() {
		settings = (TextView) findViewById(R.id.textView1);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("\nEnable Update: " + preferences.getBoolean("enable_update", false));
		builder.append("\nUpdate Frequency: " + preferences.getString("update_frequency", "NULL"));
		builder.append("\nUpdate On Launch: " + preferences.getBoolean("enable_update_on_launch", false));
		builder.append("\nUnits: " + preferences.getString("units", "NULL"));
		builder.append("\nShow forecast for: " + preferences.getString("forecast_days", "NULL") + " days");
		
		settings.setText(builder.toString());
	}
}
